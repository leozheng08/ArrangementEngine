package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinitionCache;
import cn.tongdun.kunpeng.api.infrastructure.config.ConfigManager;
import cn.tongdun.kunpeng.client.data.RiskResponse;
import cn.tongdun.kunpeng.common.data.*;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    private PolicyDefinitionCache policyDefinitionCache;

    @Autowired
    private PolicyCache policyCache;


    @Resource(name="configManager")
    ConfigManager configManager;

    @Override
    public boolean invoke(AbstractFraudContext context, RiskResponse response, Map<String, String> request) {

        String partnerCode = context.getPartnerCode();
        String appName = context.getAppName();
        String eventId = request.get(RequestParamName.EVENT_ID);
        String policyVersion = request.get(RequestParamName.POLICY_VERSION);

        if(StringUtils.isBlank(eventId)){
            response.setReason_code(ReasonCode.REQ_DATA_TYPE_ERROR.toString());
            return false;
        }

        String policyUuid = null;
        if(StringUtils.isNotBlank(policyVersion)) {
            policyUuid = policyCache.getPolicyUuid(partnerCode, appName, eventId, policyVersion);
        } else {
            policyUuid = policyDefinitionCache.getPolicyUuid(partnerCode, appName, eventId);
        }

        if(StringUtils.isBlank(policyUuid)){ //todo 增加404子码的细分
            response.setReason_code(ReasonCode.POLICY_NOT_EXIST.toString());
            return false;
        }

        Policy policy = policyCache.get(policyUuid);
        if(policy == null){ //todo 增加404子码的细分
            response.setReason_code(ReasonCode.POLICY_NOT_EXIST.toString());
            return false;
        }

        context.setPolicyUuid(policyUuid);
        context.setEventType(policy.getEventType());
        context.setAppName(policy.getAppName());
        context.setAppType(policy.getAppType());

        BizScenario bizScenario = createBizScenario(context);
        context.setBizScenario(bizScenario);

        return true;
    }


    private BizScenario createBizScenario(AbstractFraudContext context){
        BizScenario bizScenario = new BizScenario();
        bizScenario.setTenant(configManager.getProperty("tenant"));
        bizScenario.setPartner(context.getPartnerCode());
        //根据event_type区分业务类型，如credit信贷，anti_fraud反欺诈
        String businessType = configManager.getBusinessByEventType(context.getEventType());
        bizScenario.setBusiness(businessType);
        return bizScenario;
    }
}
