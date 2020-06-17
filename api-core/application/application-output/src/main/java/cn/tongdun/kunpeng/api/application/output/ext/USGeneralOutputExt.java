package cn.tongdun.kunpeng.api.application.output.ext;

import cn.tongdun.kunpeng.api.common.data.*;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultType;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.client.data.IHitRule;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.IRiskResponseFactory;
import cn.tongdun.kunpeng.client.data.ISubPolicyResult;
import cn.tongdun.kunpeng.client.data.impl.underline.RiskResponse;
import cn.tongdun.kunpeng.client.data.impl.us.PolicyResult;
import cn.tongdun.tdframework.core.extension.Extension;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: yuanhang
 * @date: 2020-06-17 14:05
 **/
//@Extension(tenant = "us",business = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class USGeneralOutputExt implements IGeneralOutputExtPt{

    @Autowired
    private DecisionResultTypeCache decisionResultTypeCache;

    @Override
    public boolean generalOutput(AbstractFraudContext context, IRiskResponse response) {

        // 对于北美的场景，考虑到规则详情已经迁移到了调用日志，保持原有的逻辑不变
        PolicyResponse policyResponse = context.getPolicyResponse();
        if (null == policyResponse) {
            return true;
        }
        response.setSuccess(policyResponse.isSuccess());
        response.setFinalDecision(policyResponse.getDecision());
        response.setFinalScore(policyResponse.getScore());
        response.setPolicyName(policyResponse.getPolicyName());
        if (policyResponse.getSubPolicyResponses() == null || (policyResponse.getSubPolicyResponses().isEmpty())) {
            return true;
        }

        List<ISubPolicyResult> policyResults = new ArrayList<ISubPolicyResult>(policyResponse.getSubPolicyResponses().size());
        List<IHitRule> rules = new ArrayList<IHitRule>();
        StringBuilder riskTypeBuilder = new StringBuilder();
        for (SubPolicyResponse subPolicyResponse : policyResponse.getSubPolicyResponses()) {
            if (StringUtils.isNotBlank(subPolicyResponse.getRiskType())) {
                DecisionResultType decisionResultType = decisionResultTypeCache.get(subPolicyResponse.getDecision());
                if(decisionResultType != null && decisionResultType.isRisky()){
                    if (riskTypeBuilder.length() > 0) {
                        riskTypeBuilder.append(",");
                    }
                    riskTypeBuilder.append(subPolicyResponse.getRiskType()).append("_").append(decisionResultType.getCode().toLowerCase());
                }
            }
            ISubPolicyResult subPolicyResult = response.getFactory().newSubPolicyResult();
            subPolicyResult.setSubPolicyUuid(subPolicyResponse.getSubPolicyUuid());
            subPolicyResult.setSubPolicyName(subPolicyResponse.getSubPolicyName());
            subPolicyResult.setPolicyMode(subPolicyResponse.getPolicyMode());
            subPolicyResult.setPolicyScore(subPolicyResponse.getScore());
            subPolicyResult.setRiskType(subPolicyResponse.getRiskType());
            subPolicyResult.setPolicyDecision(subPolicyResponse.getDecision());

            List<IHitRule> hitRuleList = getHitRules(response.getFactory(),subPolicyResponse);
            if(hitRuleList != null && !hitRuleList.isEmpty()) {
                subPolicyResult.setHitRules(hitRuleList);
                rules.addAll(hitRuleList);
            }
            policyResults.add(subPolicyResult);
        }
        response.setSubPolicys(policyResults);
        if (rules.size() > 0) {
            ((RiskResponse)response).setHitRules(rules);
        }

        Map customPolicyResult = Maps.newHashMap();
        customPolicyResult.put("deviceInfo", context.getDeviceInfo());
        response.setCustomPolicyResult(customPolicyResult);

        List<PolicyResult> policySet = Lists.newArrayList();

        return true;
    }

    private List<IHitRule> getHitRules(IRiskResponseFactory factory, SubPolicyResponse subPolicyResponse){
        List<RuleResponse> ruleResponseList = subPolicyResponse.getRuleResponses();
        if(ruleResponseList == null || ruleResponseList.isEmpty()){
            return null;
        }

        List<IHitRule> hitRuleList = new ArrayList<>();
        for(RuleResponse ruleResponse:ruleResponseList){
            if (ruleResponse.isHit()) {
                IHitRule hitRule = createHitRule(factory,ruleResponse);
                hitRuleList.add(hitRule);
            }
        }

        return hitRuleList;
    }

    private IHitRule createHitRule(IRiskResponseFactory factory,RuleResponse ruleResponse){
        IHitRule hitRule = factory.newHitRule();
        hitRule.setId(ruleResponse.getId());
        hitRule.setName(ruleResponse.getName());
        hitRule.setUuid(ruleResponse.getUuid());
        hitRule.setParentUuid(ruleResponse.getParentUuid());
        hitRule.setScore(ruleResponse.getScore());
        //决策结果,如Accept、Review、Reject
        hitRule.setDecision(ruleResponse.getDecision());
        return hitRule;
    }
}
