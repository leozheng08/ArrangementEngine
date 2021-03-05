package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.data.SubReasonCode;
import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.constant.DeleteStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinitionCache;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.config.IBaseConfig;
import cn.tongdun.kunpeng.api.common.config.ILocalEnvironment;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 取得策略uuid
 * @Author: liang.chen
 * @Date: 2020/2/17 下午11:09
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.CHECK, order = 300)
public class GetPolicyUuidStep implements IRiskStep {


    @Autowired
    private ILocalEnvironment localEnvironment;

    @Autowired
    private PolicyDefinitionCache policyDefinitionCache;

    @Autowired
    private PolicyCache policyCache;


    @Autowired
    private IBaseConfig baseConfig;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {

        //如果合作方为空则设置默认合作方
        setDefaultPartnerCode(context);
        //如果appName为空则设置默认appName
        setDefaultAppName(context);

        String partnerCode = context.getPartnerCode();
        String appName = context.getAppName();
        String eventId = request.getEventId();
        String policyVersion = request.getPolicyVersion();

        if(StringUtils.isBlank(eventId)){
            response.setReasonCode(ReasonCode.REQ_DATA_TYPE_ERROR.toString()+":"+"eventId值为空");
            return false;
        }
        context.setEventId(eventId);
        context.setPolicyVersion(policyVersion);

        String policyUuid = null;
        if(StringUtils.isNotBlank(policyVersion)) {
            policyUuid = policyCache.getPolicyUuid(partnerCode, appName, eventId, policyVersion);
        } else {
            PolicyDefinition policyDefinition= policyDefinitionCache.getPolicyDefinition(partnerCode, appName, eventId);
            //策略定义不存在
            if(policyDefinition == null){
                logger.warn(TraceUtils.getFormatTrace()+",policyDefinition == null,{},partnerCode:{},eventId:{}",ReasonCode.POLICY_NOT_EXIST_SUB.toString(), partnerCode, eventId);
                context.addSubReasonCode(new SubReasonCode(ReasonCode.POLICY_NOT_EXIST_SUB.getCode(), ReasonCode.POLICY_NOT_EXIST_SUB.getDescription(), "决策引擎执行"));
                response.setReasonCode(ReasonCode.POLICY_NOT_EXIST.toString()+":"+ReasonCode.POLICY_NOT_EXIST_SUB.getDescription());
                return false;
            }

            //策略定义已删除
            if(DeleteStatusEnum.INVALID.getCode() == policyDefinition.isDeleted()){
                logger.warn(TraceUtils.getFormatTrace()+"{},partnerCode:{},eventId:{}",ReasonCode.POLICY_DELETED.toString(), partnerCode, eventId);
                context.addSubReasonCode(new SubReasonCode(ReasonCode.POLICY_DELETED.getCode(), ReasonCode.POLICY_DELETED.getDescription(), "决策引擎执行"));
                response.setReasonCode(ReasonCode.POLICY_NOT_EXIST.toString()+":"+ReasonCode.POLICY_DELETED.getDescription());
                return false;
            }

            //策略定义已关闭
            if(CommonStatusEnum.CLOSE.getCode() == policyDefinition.getStatus()){
                logger.warn(TraceUtils.getFormatTrace()+"{},partnerCode:{},eventId:{}",ReasonCode.POLICY_CLOSED.toString(), partnerCode, eventId);
                context.addSubReasonCode(new SubReasonCode(ReasonCode.POLICY_CLOSED.getCode(), ReasonCode.POLICY_CLOSED.getDescription(), "决策引擎执行"));
                response.setReasonCode(ReasonCode.POLICY_NOT_EXIST.toString()+":"+ReasonCode.POLICY_CLOSED.getDescription());
                return false;
            }
            policyUuid = policyDefinition.getCurrVersionUuid();
        }

        //策略不存在
        if(StringUtils.isBlank(policyUuid)){
            logger.warn(TraceUtils.getFormatTrace()+",policyUuid isBlank,{},partnerCode:{},eventId:{}",ReasonCode.POLICY_NOT_EXIST_SUB.toString(), partnerCode, eventId);
            context.addSubReasonCode(new SubReasonCode(ReasonCode.POLICY_NOT_EXIST_SUB.getCode(), ReasonCode.POLICY_NOT_EXIST_SUB.getDescription(), "决策引擎执行"));
            response.setReasonCode(ReasonCode.POLICY_NOT_EXIST.toString()+":"+ReasonCode.POLICY_NOT_EXIST_SUB.getDescription());
            return false;
        }
        Policy policy = policyCache.get(policyUuid);
        if(policy == null){
            logger.warn(TraceUtils.getFormatTrace()+",policy == null,{},partnerCode:{},eventId:{}",ReasonCode.POLICY_NOT_EXIST_SUB.toString(), partnerCode, eventId);
            context.addSubReasonCode(new SubReasonCode(ReasonCode.POLICY_NOT_EXIST_SUB.getCode(), ReasonCode.POLICY_NOT_EXIST_SUB.getDescription(), "决策引擎执行"));
            response.setReasonCode(ReasonCode.POLICY_NOT_EXIST.toString()+":"+ReasonCode.POLICY_NOT_EXIST_SUB.getDescription());
            return false;
        }

        //策略已删除
        if(DeleteStatusEnum.INVALID.getCode() == policy.isDeleted()){
            logger.warn(TraceUtils.getFormatTrace()+"{},partnerCode:{},eventId:{}",ReasonCode.POLICY_DELETED.toString(), partnerCode, eventId);
            context.addSubReasonCode(new SubReasonCode(ReasonCode.POLICY_DELETED.getCode(), ReasonCode.POLICY_DELETED.getDescription(), "决策引擎执行"));
            response.setReasonCode(ReasonCode.POLICY_NOT_EXIST.toString()+":"+ReasonCode.POLICY_DELETED.getDescription());
            return false;
        }

        //策略已关闭
        if(CommonStatusEnum.CLOSE.getCode() == policy.getStatus()){
            logger.warn(TraceUtils.getFormatTrace()+"{},partnerCode:{},eventId:{}",ReasonCode.POLICY_CLOSED.toString(), partnerCode, eventId);
            context.addSubReasonCode(new SubReasonCode(ReasonCode.POLICY_CLOSED.getCode(), ReasonCode.POLICY_CLOSED.getDescription(), "决策引擎执行"));
            response.setReasonCode(ReasonCode.POLICY_NOT_EXIST.toString()+":"+ReasonCode.POLICY_CLOSED.getDescription());
            return false;
        }


        context.setPolicyUuid(policyUuid);
        context.setEventType(policy.getEventType());


        BizScenario bizScenario = createBizScenario(context);
        context.setBizScenario(bizScenario);

        return true;
    }


    private void setDefaultPartnerCode(AbstractFraudContext context){
        if(StringUtils.isNotBlank(context.getPartnerCode())){
            return;
        }

        //如果不传partner_code，则按默认值处理
        context.setPartnerCode(Constant.DEFAULT_PARTNER);
    }

    private void setDefaultAppName(AbstractFraudContext context){
        if(StringUtils.isNotBlank(context.getAppName())) {
            return;
        }
        context.setAppName(Constant.DEFAULT_APP_NAME);
    }


    private BizScenario createBizScenario(AbstractFraudContext context){
        BizScenario bizScenario = new BizScenario();
        bizScenario.setTenant(localEnvironment.getTenant());
        bizScenario.setPartner(context.getPartnerCode());
        if (StringUtils.isNotEmpty(bizScenario.getPartner()) && "derica".equals(bizScenario.getPartner())) {
            bizScenario.setPartner("globalegrow");
        }
        //根据event_type区分业务类型，如credit信贷，anti_fraud反欺诈
        String businessType = baseConfig.getBusinessByEventType(context.getEventType());
        bizScenario.setBusiness(businessType);
        return bizScenario;
    }
}
