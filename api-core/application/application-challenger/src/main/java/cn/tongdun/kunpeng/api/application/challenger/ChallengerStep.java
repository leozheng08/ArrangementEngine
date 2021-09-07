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
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinitionCache;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 挑战者，根据冠军、挑战者的权重决定当前是运行冠军还是挑战者
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.CHECK, order = 310)
public class ChallengerStep implements IRiskStep {

    private static final Logger logger = LoggerFactory.getLogger(ChallengerStep.class);

    @Autowired
    private PolicyDefinitionCache policyDefinitionCache;

    @Autowired
    private PolicyChallengerCache policyChallengerCache;

    @Autowired
    private PolicyCache policyCache;

    /**
     * 分流形式
     * @param context
     * @param response
     * @param request
     * @return
     */
    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        String partnerCode = context.getPartnerCode();
        String appName = context.getAppName();
        String eventId = context.getEventId();
        String originPolicyUuid = context.getPolicyUuid();

        PolicyDefinition policyDefinition= policyDefinitionCache.getPolicyDefinition(partnerCode, appName, eventId);
        //策略定义不存在
        if(policyDefinition == null){
            logger.warn(TraceUtils.getFormatTrace()+",policyDefinition is null,{},partnerCode:{},eventId:{}", ReasonCode.POLICY_NOT_EXIST_SUB.toString(), partnerCode, eventId);
            context.addSubReasonCode(new SubReasonCode(ReasonCode.POLICY_NOT_EXIST_SUB.getCode(), ReasonCode.POLICY_NOT_EXIST_SUB.getDescription(), "决策引擎执行"));
            return false;
        }

        String policyUuid = policyChallengerCache.getNextPolicyUuid(policyDefinition.getUuid());
        //没有挑战者任务时，返回的policyUuid为空
        if(StringUtils.isBlank(policyUuid)){
            return true;
        }


        Policy policy = policyCache.get(policyUuid);
        if(policy == null){
            logger.warn(TraceUtils.getFormatTrace()+",policy is null,{},partnerCode:{},eventId:{}",ReasonCode.POLICY_NOT_EXIST_SUB.toString(), partnerCode, eventId);
            context.addSubReasonCode(new SubReasonCode(ReasonCode.POLICY_NOT_EXIST_SUB.getCode(), ReasonCode.POLICY_NOT_EXIST_SUB.getDescription(), "决策引擎执行"));
            return false;
        }

        //策略已删除
        if(DeleteStatusEnum.INVALID.getCode() == policy.isDeleted()){
            logger.warn(TraceUtils.getFormatTrace()+"{},partnerCode:{},eventId:{}",ReasonCode.POLICY_DELETED.toString(), partnerCode, eventId);
            context.addSubReasonCode(new SubReasonCode(ReasonCode.POLICY_DELETED.getCode(), ReasonCode.POLICY_DELETED.getDescription(), "决策引擎执行"));
            return false;
        }

        //策略已关闭
        if(CommonStatusEnum.CLOSE.getCode() == policy.getStatus()){
            logger.warn(TraceUtils.getFormatTrace()+"{},partnerCode:{},eventId:{}",ReasonCode.POLICY_CLOSED.toString(), partnerCode, eventId);
            context.addSubReasonCode(new SubReasonCode(ReasonCode.POLICY_CLOSED.getCode(), ReasonCode.POLICY_CLOSED.getDescription(), "决策引擎执行"));
            return false;
        }

        //存在挑战者任务
        if (StringUtils.isNotBlank(policyUuid)) {
            PolicyChallenger.Config config = policyChallengerCache.getConfig(policyUuid);
            context.setChallengerTag(config.getChallengerTag());
            if(StringUtils.equals(originPolicyUuid, policyUuid)){
                //冠军
                context.setChallenger(false);
            }else {
                //挑战者
                logger.info(TraceUtils.getFormatTrace()+"[Challenger] policyUuid changed to challenger version! origin: {}, now: {}", originPolicyUuid, policyUuid);
                context.setPolicyUuid(policyUuid);
                context.setChallenger(true);
            }
        }
        return true;
    }
}
