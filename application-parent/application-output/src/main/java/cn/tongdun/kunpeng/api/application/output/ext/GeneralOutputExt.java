package cn.tongdun.kunpeng.api.application.output.ext;

import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultType;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.client.data.HitRule;
import cn.tongdun.kunpeng.client.data.PolicyResult;
import cn.tongdun.kunpeng.client.data.RiskResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.BizScenario;
import cn.tongdun.kunpeng.common.data.PolicyResponse;
import cn.tongdun.kunpeng.common.data.SubPolicyResponse;
import cn.tongdun.tdframework.core.extension.Extension;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 标准输出扩展点默认实现
 * @Author: liang.chen
 * @Date: 2020/2/10 下午9:44
 */
@Extension(business = BizScenario.DEFAULT,tenant = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class GeneralOutputExt implements IGeneralOutputExtPt {


    @Autowired
    private DecisionResultTypeCache decisionResultTypeCache;

    @Override
    public boolean generalOutput(AbstractFraudContext context,RiskResponse response){

        PolicyResponse policyResponse = context.getPolicyResponse();
        if(policyResponse == null){
            return true;
        }
        response.setSuccess(policyResponse.isSuccess());

        response.setFinal_decision(policyResponse.getDecision());
        response.setFinal_score(policyResponse.getScore());

        response.setPolicy_set_name(policyResponse.getPolicyName());


        if (policyResponse.getSubPolicyResponses() == null || (policyResponse.getSubPolicyResponses().isEmpty())) {
            return true;
        }


        List<PolicyResult> policyResults = new ArrayList<PolicyResult>(policyResponse.getSubPolicyResponses().size());
        List<HitRule> rules = new ArrayList<HitRule>();
        StringBuilder riskTypeBuilder = new StringBuilder();
        for (SubPolicyResponse subPolicyResponse : policyResponse.getSubPolicyResponses()) {
            if (StringUtils.isNotBlank(subPolicyResponse.getRiskType())) {
                DecisionResultType decisionResultTyp = decisionResultTypeCache.get(subPolicyResponse.getDecision());

                if(decisionResultTyp != null && decisionResultTyp.isRisky()){
                    if (riskTypeBuilder.length() > 0) {
                        riskTypeBuilder.append(",");
                    }
                    riskTypeBuilder.append(subPolicyResponse.getRiskType()).append("_").append(decisionResultTyp.getCode().toLowerCase());
                }
            }
            PolicyResult policyResult = new PolicyResult();
            policyResult.setPolicy_uuid(subPolicyResponse.getSubPolicyUuid());
            policyResult.setPolicy_name(subPolicyResponse.getSubPolicyName());
            policyResult.setPolicy_mode(subPolicyResponse.getPolicyMode());
            policyResult.setPolicy_score(subPolicyResponse.getScore());
            policyResult.setRisk_type(subPolicyResponse.getRiskType());
            policyResult.setPolicy_decision(subPolicyResponse.getDecision());
            if(subPolicyResponse.getHitRules()!= null && !subPolicyResponse.getHitRules().isEmpty()) {
                policyResult.setHit_rules(subPolicyResponse.getHitRules());
                rules.addAll(subPolicyResponse.getHitRules());
            }
            policyResults.add(policyResult);
        }
        response.setPolicy_set(policyResults);
        if (rules.size() > 0) {
            response.setHit_rules(rules);
        }

        //风险最大的子策略
        SubPolicyResponse finalResponse = policyResponse.getFinalSubPolicyResponse();
        if(finalResponse != null) {
            response.setPolicy_name(finalResponse.getSubPolicyName());
            response.setRisk_type(riskTypeBuilder.toString());
        }
        return true;
    }
}
