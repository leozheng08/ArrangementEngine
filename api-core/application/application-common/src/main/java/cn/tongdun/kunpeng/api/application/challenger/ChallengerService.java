package cn.tongdun.kunpeng.api.application.challenger;

import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
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
import cn.tongdun.kunpeng.share.config.IConfigRepository;
import cn.tongdun.tdframework.core.concurrent.ThreadService;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ChallengerService {

    private Logger logger = LoggerFactory.getLogger(ChallengerService.class);

    @Autowired
    private IRiskService riskService;

    /**
     * ruleExecute 线程池
     */
    private ExecutorService executeThreadPool;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private PolicyDefinitionCache policyDefinitionCache;

    @Autowired
    private PolicyCache policyCache;

    @Autowired
    private PolicyChallengerCache policyChallengerCache;

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
     * invoke challenger by challengerType
     *
     * @param riskRequest
     * @param bizName
     */
    public void invoke(RiskRequest riskRequest, String bizName) {
        try {
            execute(riskRequest, bizName);
        } catch (Exception e) {
            logger.info("ChallengerService execute 执行异常:{}", e);
        }

    }

    /**
     * invoke challenger by challengerType
     *
     * @param riskRequest
     * @param bizName
     */
    private void execute(RiskRequest riskRequest, String bizName) {
        RiskRequest request = new RiskRequest();
        BeanUtils.copyProperties(riskRequest, request);
        String partnerCode = riskRequest.getPartnerCode();
        String appName = riskRequest.getAppName();
        String eventId = riskRequest.getEventId();

        PolicyDefinition policyDefinition = policyDefinitionCache.getPolicyDefinition(partnerCode, appName, eventId);
        //策略定义不存在
        if (policyDefinition == null) {
            return;
        }

        PolicyChallenger policyChallenger = policyChallengerCache.get(policyDefinition.getUuid());
        //没有挑战者任务时，直接返回
        if (policyChallenger == null) {
            return;
        }
        //不是复制类型流量不处理
        if (StringUtils.isEmpty(policyChallenger.getChallengerType()) || !ChallengerTypeEnum.COPY.getType().equals(policyChallenger.getChallengerType())) {
            return;
        }
        //还未生效
        if (policyChallenger.getStartTime() != null && System.currentTimeMillis() < policyChallenger.getStartTime().getTime()) {
            return;
        }
        //已失效
        if (policyChallenger.getEndTime() != null && System.currentTimeMillis() > policyChallenger.getEndTime().getTime()) {
            return;
        }
        //暂停、关闭等
        if(policyChallenger.getStatus() == null || policyChallenger.getStatus() == CommonStatusEnum.CLOSE.getCode()|| policyChallenger.getStatus() == CommonStatusEnum.CLOSE.getCode()){
            return ;
        }
        List<PolicyChallenger.Config> configs = policyChallenger.getChallengerConfig();
        /**
         * executeThreadPool 异步执行
         */
        if (CollectionUtils.isNotEmpty(configs)) {
            for (PolicyChallenger.Config config : configs) {
                if (StringUtils.isNotEmpty(config.getChallengerTag()) && !"champion".equals(config.getChallengerTag())) {
                    if (StringUtils.isNotEmpty(config.getVersionUuid())) {
                        Policy policy = policyCache.get(config.getVersionUuid());
                        request.setPolicyVersion(policy.getVersion());
                        executeThreadPool.submit(new Callable<Boolean>() {
                            @Override
                            public Boolean call() {
                                try {
                                    riskService.riskService(request, bizName);
                                    return true;
                                } catch (Exception e) {
                                    return false;
                                }
                            }
                        });
                    }

                }
            }
        }

    }
}
