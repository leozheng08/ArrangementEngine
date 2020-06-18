package cn.tongdun.kunpeng.api.application.output.ext;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.PolicyResponse;
import cn.tongdun.kunpeng.api.common.data.RuleResponse;
import cn.tongdun.kunpeng.api.common.data.SubPolicyResponse;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultType;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.client.data.IHitRule;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.IRiskResponseFactory;
import cn.tongdun.kunpeng.client.data.ISubPolicyResult;
import cn.tongdun.kunpeng.client.data.impl.us.PolicyResult;
import cn.tongdun.tdframework.core.extension.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: yuanhang
 * @date: 2020-06-17 14:05
 **/
@Extension(tenant = "us",business = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
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
        if (policyResponse.getSubPolicyResponses() == null || (policyResponse.getSubPolicyResponses().isEmpty())) {
            return true;
        }

        // 返回值中注入通用参数（兼容规则详情，不通过接口输出）
        injectCommonResponse(response, policyResponse);

        // 返回值中注入北美/天策参数
        injectUSResponse(context, response, policyResponse);

        return true;
    }

    private void injectUSResponse(AbstractFraudContext context, IRiskResponse response, PolicyResponse policyResponse) {
        // 先填充deviceInfo信息，responseAdjust中填充业务对接中的映射
        Map customPolicyResult = Maps.newHashMap();
        customPolicyResult.put("deviceInfo", context.getDeviceInfo());
        response.setCustomPolicyResult(customPolicyResult);
        // 先不知道是干嘛的，先写死
        response.setIgnoreReq(false);

        // 填充policyDetailResult
        PolicyResult policyDetailResult = new PolicyResult();
        policyDetailResult.setSuccess(policyResponse.isSuccess());
        policyDetailResult.setPolicyName(policyResponse.getPolicyName());
        policyDetailResult.setFinalScore(policyResponse.getScore());
        policyDetailResult.setSpendTime(policyResponse.getCostTime());
        policyDetailResult.setSeqId(context.getSeqId());
        policyDetailResult.setFinalDealType(policyResponse.getDecision());
        policyDetailResult.setFinalDealTypeName(policyResponse.getDecision());
        // TODO 确认grade是啥
        policyDetailResult.setFinalDealTypeGrade("");
        // TODO 确认这个map是啥
        policyDetailResult.setNusspecialMap(Maps.newHashMap());
        // 暂时填充策略名称名称
        policyDetailResult.setPolicySetName(context.getPolicyResponse().getPolicyName());
        // 填充自策略
        policyDetailResult.setPolicySet(buildUSPolicySets(response, policyResponse));
        // 填充riskType
        List<String> riskTypes = Lists.newArrayList();
        policyResponse.getSubPolicyResponses().stream().forEach(subPolicyResponse -> {
            DecisionResultType decisionResultType = decisionResultTypeCache.get(subPolicyResponse.getDecision());
            if(decisionResultType != null && decisionResultType.isRisky()){
                riskTypes.add(subPolicyResponse.getRiskType());
            }
        });
        policyDetailResult.setRiskType(riskTypes);
        // 规则详情不要了
        policyDetailResult.setHitRules(null);
        response.setPolicyDetailResult(policyDetailResult);
    }

    private void injectCommonResponse(IRiskResponse response, PolicyResponse policyResponse) {
        response.setSuccess(policyResponse.isSuccess());
        response.setFinalDecision(policyResponse.getDecision());
        response.setFinalScore(policyResponse.getScore());
        response.setPolicyName(policyResponse.getPolicyName());

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

    private List<IHitRule> buildUSHitRules(IRiskResponseFactory factory, SubPolicyResponse subPolicyResponse) {
        List<RuleResponse> ruleResponseList = subPolicyResponse.getRuleResponses();
        if (CollectionUtils.isEmpty(ruleResponseList)) {
            return null;
        }
        List<IHitRule> hitRuleList = Lists.newArrayList();
        ruleResponseList.stream().forEach(ruleResponse -> {
            if (ruleResponse.isHit()) {
                hitRuleList.add(buildUSHitRule(factory, ruleResponse));
            }
        });
        return hitRuleList;
    }

    /**
     * // 规则详情不要了
     * 构建命中的规则
     * @param factory
     * @param ruleResponse
     * @return
     */
    private IHitRule buildUSHitRule(IRiskResponseFactory factory, RuleResponse ruleResponse){
        IHitRule hitRule = factory.newHitRule();
        hitRule.setUuid(ruleResponse.getUuid());
        hitRule.setName(ruleResponse.getName());
        hitRule.setEnName("");
        hitRule.setDecision("");
        hitRule.setDealType("");
        hitRule.setParentUuid(ruleResponse.getParentUuid());
        hitRule.setScore(ruleResponse.getScore());
        return hitRule;
    }

    private List<ISubPolicyResult> buildUSPolicySets(IRiskResponse response, PolicyResponse policyResponse) {
        List<ISubPolicyResult> subPolicyResults = new ArrayList<>(policyResponse.getSubPolicyResponses().size());
        for (SubPolicyResponse subPolicyResponse : policyResponse.getSubPolicyResponses()) {
            ISubPolicyResult subPolicyResult = response.getFactory().newSubPolicyResult();
            // 填充子策略RiskType字段
            if (StringUtils.isNotEmpty(subPolicyResponse.getRiskType())) {
                DecisionResultType decisionResultType = decisionResultTypeCache.get(subPolicyResponse.getDecision());
                if(decisionResultType != null && decisionResultType.isRisky()){
                    subPolicyResult.setRiskType(subPolicyResponse.getRiskType());
                }
                subPolicyResult.setSubPolicyUuid(subPolicyResponse.getSubPolicyUuid());
                subPolicyResult.setSubPolicyName(subPolicyResponse.getSubPolicyName());
                subPolicyResult.setPolicyScore(subPolicyResponse.getScore());
                subPolicyResult.setPolicyMode(subPolicyResponse.getPolicyMode());
                subPolicyResult.setDealType(subPolicyResponse.getDecision());
                subPolicyResult.setHitRules(buildUSHitRules(response.getFactory(), subPolicyResponse));
            }
            subPolicyResults.add(subPolicyResult);
        }
        return subPolicyResults;
    }
}
