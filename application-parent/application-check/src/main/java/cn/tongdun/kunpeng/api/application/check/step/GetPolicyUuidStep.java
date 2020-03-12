package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinitionCache;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.config.IBaseConfig;
import cn.tongdun.kunpeng.common.config.ILocalEnvironment;
import cn.tongdun.kunpeng.common.data.*;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

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
        setDefaultPartnerCode(context, request);

        String partnerCode = context.getPartnerCode();
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
            policyUuid = policyCache.getPolicyUuid(partnerCode, eventId, policyVersion);
        } else {
            policyUuid = policyDefinitionCache.getPolicyUuid(partnerCode, eventId);
        }

        if(StringUtils.isBlank(policyUuid)){ //todo 增加404子码的细分
            response.setReasonCode(ReasonCode.POLICY_NOT_EXIST.toString());
            return false;
        }

        Policy policy = policyCache.get(policyUuid);
        if(policy == null){ //todo 增加404子码的细分
            response.setReasonCode(ReasonCode.POLICY_NOT_EXIST.toString());
            return false;
        }

        context.setPolicyUuid(policyUuid);
        context.setEventType(policy.getEventType());


        BizScenario bizScenario = createBizScenario(context);
        context.setBizScenario(bizScenario);

        return true;
    }


    private void setDefaultPartnerCode(AbstractFraudContext context,RiskRequest request){
        if(StringUtils.isNotBlank(context.getPartnerCode())){
            return;
        }

        //如果不传partner_code，则按默认值处理
        if(StringUtils.isBlank(request.getPartnerCode())) {
            context.setPartnerCode(Constant.DEFAULT_PARTNER);
        } else {
            context.setPartnerCode(request.getPartnerCode());
        }
    }

    private BizScenario createBizScenario(AbstractFraudContext context){
        BizScenario bizScenario = new BizScenario();
        bizScenario.setTenant(localEnvironment.getTenant());
        bizScenario.setPartner(context.getPartnerCode());
        //根据event_type区分业务类型，如credit信贷，anti_fraud反欺诈
        String businessType = baseConfig.getBusinessByEventType(context.getEventType());
        bizScenario.setBusiness(businessType);
        return bizScenario;
    }
}
