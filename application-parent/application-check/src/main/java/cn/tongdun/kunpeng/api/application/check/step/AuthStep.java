package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinitionCache;
import cn.tongdun.kunpeng.api.infrastructure.config.ConfigManager;
import cn.tongdun.kunpeng.client.data.RiskResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.ReasonCode;
import cn.tongdun.kunpeng.common.data.RequestParamName;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 合作方认证
 * @Author: liang.chen
 * @Date: 2020/2/17 下午11:09
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.CHECK, order = 200)
public class AuthStep implements IRiskStep {

    @Autowired
    private PolicyDefinitionCache policyDefinitionCache;

    @Autowired
    private PolicyCache policyCache;

    @Autowired
    private ConfigManager configManager;

    @Override
    public boolean invoke(AbstractFraudContext context, RiskResponse response, Map<String, String> request) {

        String partnerCode = request.get(RequestParamName.PARTNER_CODE);
        String secretKey = request.get(RequestParamName.SECRET_KEY);


        if(StringUtils.isAnyBlank(partnerCode,secretKey)){
            response.setReason_code(ReasonCode.AUTH_FAILED.toString());
            return false;
        }

        boolean enableAuth = configManager.isConfig("riskService.auth.enable",false);
        if(enableAuth){
            //todo 验证secretKey是否正确
        }


        context.setPartnerCode(partnerCode);
        context.setSecretKey(secretKey);
        return true;
    }
}
