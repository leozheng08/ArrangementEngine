package cn.tongdun.kunpeng.api.application.challenger;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.data.SubReasonCode;
import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.constant.DeleteStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.PolicyChallenger;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.PolicyChallengerCache;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.constant.ChallengerTypeEnum;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinitionCache;
import cn.tongdun.kunpeng.client.api.IRiskService;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.ThreadService;
import cn.tongdun.tdframework.core.pipeline.Step;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 挑战者，根据冠军、挑战者复制流量处理
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.CHECK, order = 320)
public class ChallengerCopyStep implements IRiskStep {

    private static final Logger logger = LoggerFactory.getLogger(ChallengerCopyStep.class);

    @Autowired
    private PolicyDefinitionCache policyDefinitionCache;

    @Autowired
    private PolicyChallengerCache policyChallengerCache;

    @Autowired
    private PolicyCache policyCache;

    @Autowired
    private IRiskService riskService;

    @Autowired
    private DelayQueueCache delayQueueCache;

    /**
     * ruleExecute 线程池
     */
    private ExecutorService executeThreadPool;

    @Autowired
    private ThreadService threadService;

    @Value("${challenger.delay.time}")
    private int challengerDelayTime = 1;

    @PostConstruct
    public void init() {
        this.executeThreadPool = threadService.createThreadPool(
                32,
                128,
                30L,
                TimeUnit.MINUTES,
                10,
                "challengerExecute");
    }


    /**
     * 复制流量执行
     *
     * @param context
     * @param response
     * @param request
     * @return
     */
    private boolean execute(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        String partnerCode = request.getPartnerCode();
        String appName = request.getAppName();
        String eventId = request.getEventId();
        Object challengerType = request.getFieldValues().get("challengerType");
        Object challengerTag = request.getFieldValues().get("challengerTag");
        if (Objects.nonNull(challengerType)) {
            if (Objects.nonNull(challengerTag)) {
                context.setChallengerTag(challengerTag.toString());
            }
            return true;
        }
        PolicyDefinition policyDefinition = policyDefinitionCache.getPolicyDefinition(partnerCode, appName, eventId);
        //策略定义不存在
        if (policyDefinition == null) {
            return true;
        }

        PolicyChallenger policyChallenger = policyChallengerCache.get(policyDefinition.getUuid());
        //没有挑战者任务时，直接返回
        if (policyChallenger == null) {
            return true;
        }
        //不是复制类型流量不处理
        if (StringUtils.isEmpty(policyChallenger.getChallengerType()) || !ChallengerTypeEnum.COPY.getType().equals(policyChallenger.getChallengerType())) {
            return true;
        }
        //还未生效
        if (policyChallenger.getStartTime() != null && System.currentTimeMillis() < policyChallenger.getStartTime().getTime()) {
            return true;
        }
        //已失效
        if (policyChallenger.getEndTime() != null && System.currentTimeMillis() > policyChallenger.getEndTime().getTime()) {
            return true;
        }
        //暂停、关闭等
        if (policyChallenger.getStatus() == null || policyChallenger.getStatus() == CommonStatusEnum.STOP.getCode() || policyChallenger.getStatus() == CommonStatusEnum.CLOSE.getCode()) {
            return true;
        }
        List<PolicyChallenger.Config> configs = policyChallenger.getChallengerConfig();
        /**
         * executeThreadPool 异步执行
         */
        if (CollectionUtils.isNotEmpty(configs)) {
            for (PolicyChallenger.Config config : configs) {
                if (StringUtils.isNotEmpty(config.getChallengerTag()) && !"champion".equals(config.getChallengerTag())) {
                    RiskRequest requestData = JSON.parseObject(JSON.toJSONString(request), RiskRequest.class);
                    requestData.setSeqId(null);
                    requestData.getFieldValues().put("originalSeqId", context.getSeqId());
                    requestData.getFieldValues().put("challengerType", "copy");
                    requestData.getFieldValues().put("challengerTag", config.getChallengerTag());
                    if (StringUtils.isNotEmpty(config.getVersionUuid())) {
                        Policy policy = policyCache.get(config.getVersionUuid());
                        if (Objects.nonNull(policy)) {
                            requestData.setPolicyVersion(policy.getVersion());
                            delayQueueCache.put(new ChallengerTask("challenger", challengerDelayTime, TimeUnit.MILLISECONDS, requestData));
                        }
                    }

                } else {
                    context.setChallengerTag(config.getChallengerTag());
                }
            }
        }
        return true;
    }

    /**
     * 流量复制形式
     *
     * @param context
     * @param response
     * @param request
     * @return
     */
    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        try {
            return execute(context, response, request);
        } catch (Exception e) {
            logger.error("ChallengerCopyStep execute执行异常:{}", e);
        }
        return true;
    }
}
