package cn.tongdun.kunpeng.api.application.output.ext;

import cn.tongdun.kunpeng.api.common.data.*;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultType;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.client.data.IHitRule;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.IRiskResponseFactory;
import cn.tongdun.kunpeng.client.data.ISubPolicyResult;
import cn.tongdun.kunpeng.client.data.impl.us.PolicyResult;
import cn.tongdun.kunpeng.client.data.impl.us.SubPolicyResult;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: yuanhang
 * @date: 2020-06-17 14:05
 **/
@Extension(tenant = "us", business = BizScenario.DEFAULT, partner = "globalegrow")
public class USGeneralOutputExt implements IGeneralOutputExtPt {

    @Autowired
    private DecisionResultTypeCache decisionResultTypeCache;

    @Override
    public boolean generalOutput(AbstractFraudContext context, IRiskResponse response) {

        // 对于北美的场景，考虑到规则详情已经迁移到了调用日志，保持原有的逻辑不变
        PolicyResponse policyResponse = context.getPolicyResponse();

        if (null == policyResponse || CollectionUtils.isEmpty(policyResponse.getSubPolicyResponses())) {
            response.setIgnoreReq(false);
            response.setSuccess(true);
            PolicyResult policyDetailResult = new PolicyResult();
            policyDetailResult.setSuccess(false);
            policyDetailResult.setSpendTime(System.currentTimeMillis() - context.getEventOccurTime().getTime());
            policyDetailResult.setSeqId(context.getSeqId());
            policyDetailResult.setFinalDealType("Accept");
            policyDetailResult.setFinalDealTypeName("通过");
            policyDetailResult.setReasonCode(buildReasonCode(context));
            response.setPolicyDetailResult(policyDetailResult);
            return true;
        }

        // 返回值中注入通用参数（兼容规则详情，不通过接口输出）
        injectCommonResponse(response, policyResponse);

        // 以下针对天策/环球易购注参

        // 先填充deviceInfo信息，responseAdjust中填充业务对接中的映射
        Map customPolicyResult = Maps.newHashMap();
        customPolicyResult.put("deviceInfo", context.getDeviceInfo());
        response.setCustomPolicyResult(customPolicyResult);
        // 流控相关，默认false
        response.setIgnoreReq(false);

        // 填充policyDetailResult
        PolicyResult policyDetailResult = new PolicyResult();
        policyDetailResult.setSuccess(policyResponse.isSuccess());
        policyDetailResult.setFinalScore(policyResponse.getScore());
        policyDetailResult.setSpendTime(policyResponse.getCostTime());
        policyDetailResult.setPolicyName(policyResponse.getPolicyName());
        policyDetailResult.setPolicyUuid(policyResponse.getPolicyUuid());
        policyDetailResult.setSeqId(context.getSeqId());
        policyDetailResult.setFinalDealType(policyResponse.getDecision());
        DecisionResultType decisionResultType = decisionResultTypeCache.get(policyResponse.getDecision());
        String name = decisionResultType == null ? "" : decisionResultType.getName();
        policyDetailResult.setFinalDealTypeName(name);
        policyDetailResult.setFinalDealTypeGrade(buildDealTypeGrade(policyResponse.getDecision() == null ? "" : policyResponse.getDecision()));
        policyDetailResult.setFlowChargeSuccessed(false);
        policyDetailResult.setEmergencySwithcOn(false);
        Map nusspecial = Maps.newHashMap();
        policyDetailResult.setNusspecialMap(nusspecial);
        // 暂时填充策略名称名称
        policyDetailResult.setPolicySetName(context.getPolicyResponse().getPolicyName());
        // 填充策略内容
        if (null == context.getFieldValues().get("subPolicys")) {
            policyDetailResult.setPolicySet(buildUSPolicySets(response, policyResponse, context));
        }
        // 填充riskType
        List<String> riskTypes = policyResponse.getSubPolicyResponses().stream().map(SubPolicyResponse::getRiskType).collect(Collectors.toList());
        policyDetailResult.setRiskType(riskTypes);
        // 规则详情不要了
        policyDetailResult.setHitRules(null);
        response.setPolicyDetailResult(policyDetailResult);

        return true;
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
                if (decisionResultType != null && decisionResultType.isRisky()) {
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

            List<IHitRule> hitRuleList = getHitRules(response.getFactory(), subPolicyResponse);
            if (hitRuleList != null && !hitRuleList.isEmpty()) {
                subPolicyResult.setHitRules(hitRuleList);
                rules.addAll(hitRuleList);
            }
            policyResults.add(subPolicyResult);
        }
        response.setSubPolicys(policyResults);
    }

    private List<IHitRule> getHitRules(IRiskResponseFactory factory, SubPolicyResponse subPolicyResponse) {
        List<RuleResponse> ruleResponseList = subPolicyResponse.getRuleResponses();
        if (ruleResponseList == null || ruleResponseList.isEmpty()) {
            return null;
        }

        List<IHitRule> hitRuleList = new ArrayList<>();
        for (RuleResponse ruleResponse : ruleResponseList) {
            if (ruleResponse.isHit()) {
                IHitRule hitRule = createHitRule(factory, ruleResponse);
                hitRuleList.add(hitRule);
            }
        }

        return hitRuleList;
    }

    private IHitRule createHitRule(IRiskResponseFactory factory, RuleResponse ruleResponse) {
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
        if (CollectionUtils.isEmpty(hitRuleList)) {
            return null;
        }
        return hitRuleList;
    }

    /**
     * // 规则详情不要了
     * 构建命中的规则
     *
     * @param factory
     * @param ruleResponse
     * @return
     */
    private IHitRule buildUSHitRule(IRiskResponseFactory factory, RuleResponse ruleResponse) {
        IHitRule hitRule = factory.newHitRule();
        hitRule.setUuid(ruleResponse.getUuid());
        hitRule.setName(ruleResponse.getName());
        hitRule.setEnName("");
        hitRule.setDecision("");
        hitRule.setDealType(ruleResponse.getDecision());
        hitRule.setParentUuid(ruleResponse.getParentUuid());
        hitRule.setScore(ruleResponse.getScore());
        return hitRule;
    }

    private List<ISubPolicyResult> buildUSPolicySets(IRiskResponse response, PolicyResponse policyResponse, AbstractFraudContext context) {
        List<ISubPolicyResult> subPolicyResults = new ArrayList<>(policyResponse.getSubPolicyResponses().size());
        for (SubPolicyResponse subPolicyResponse : policyResponse.getSubPolicyResponses()) {
            SubPolicyResult subPolicyResult = (SubPolicyResult) response.getFactory().newSubPolicyResult();
            // 填充子策略RiskType字段
            if (StringUtils.isNotEmpty(subPolicyResponse.getRiskType())) {
                subPolicyResult.setRiskType(subPolicyResponse.getRiskType());
                subPolicyResult.setPolicyScore(subPolicyResponse.getScore());
                subPolicyResult.setPolicyMode(subPolicyResponse.getPolicyMode());
                subPolicyResult.setDealType(subPolicyResponse.getDecision());
                subPolicyResult.setPolicyName(subPolicyResponse.getSubPolicyName());
                subPolicyResult.setPolicyUuid(subPolicyResponse.getSubPolicyUuid());
                if (null == context.getFieldValues().get("hitRules")) {
                    subPolicyResult.setHitRules(buildUSHitRules(response.getFactory(), subPolicyResponse));
                }
                subPolicyResult.setHitTestRules(null);
            }
            subPolicyResults.add(subPolicyResult);
        }
        return subPolicyResults;
    }

    /**
     *
     * 每种决策结果都会定义不同的决策等级,当前适配环球易购
     */
    private String buildDealTypeGrade(String decision) {
        switch (decision) {
            case "Accept":
                return "1";
            case "Review":
                return "50";
            case "Reject":
                return "100";
            default:
                return "-1";
        }
    }


    /**
     * 将kunpeng标准输出错误码映射到天策输出
     * @param context
     * @return
     */
    private String buildReasonCode(AbstractFraudContext context) {
        Set<SubReasonCode> subReasonCodes = context.getSubReasonCodes();
        for (SubReasonCode subReasonCode : subReasonCodes) {
            if (subReasonCode.getSub_code().startsWith(ReasonCode.AUTH_FAILED.getCode())) {
                return GlobalEasyGoReasonCode.AUTH_FAILED.toString();
            } else if (subReasonCode.getSub_code().startsWith(ReasonCode.REQ_DATA_TYPE_ERROR.getCode())) {
                return GlobalEasyGoReasonCode.REQ_DATA_TYPE_ERROR.toString();
            } else if (subReasonCode.getSub_code().startsWith(ReasonCode.PARAM_NULL_ERROR.getCode())) {
                return GlobalEasyGoReasonCode.PARAM_NULL_ERROR.toString();
            } else if (subReasonCode.getSub_code().startsWith(ReasonCode.PARAM_DATA_TYPE_ERROR.getCode())) {
                return GlobalEasyGoReasonCode.PARAM_DATA_TYPE_ERROR.toString();
            } else if (subReasonCode.getSub_code().startsWith(ReasonCode.PARAM_OVER_MAX_LEN.getCode())) {
                return GlobalEasyGoReasonCode.PARAM_OVER_MAX_LEN.toString();
            } else if (subReasonCode.getSub_code().startsWith(ReasonCode.PARAM_FORMAT_ERROR.getCode())) {
                return GlobalEasyGoReasonCode.PARAM_FORMAT_ERROR.toString();
            } else if (subReasonCode.getSub_code().startsWith(ReasonCode.QUERY_TIME_INTERVAL_INVALID.getCode())) {
                return GlobalEasyGoReasonCode.QUERY_TIME_INTERVAL_INVALID.toString();
            } else if (subReasonCode.getSub_code().startsWith(ReasonCode.PARAM_DATA_NOT_EXIST_ERROR.getCode())) {
                return GlobalEasyGoReasonCode.PARAM_DATA_NOT_EXIST_ERROR.toString();
            } else if (subReasonCode.getSub_code().startsWith(ReasonCode.LOAN_APPLY_ID_BLANK_ERROR.getCode())) {
                return GlobalEasyGoReasonCode.PARAM_NULL_IDENTIFY.toString();
            } else if (subReasonCode.getSub_code().startsWith(ReasonCode.FLOW_POOR.getCode())) {
                return GlobalEasyGoReasonCode.FLOW_POOR.toString();
            } else if (subReasonCode.getSub_code().startsWith(ReasonCode.NOT_BUY_SERVICE.getCode())) {
                return GlobalEasyGoReasonCode.NOT_BUY_SERVICE.toString();
            } else if (subReasonCode.getSub_code().startsWith(ReasonCode.NOT_ALLOWED.getCode())) {
                return GlobalEasyGoReasonCode.NOT_ALLOWED.toString();
            } else if (subReasonCode.getSub_code().startsWith(ReasonCode.OUT_OF_SERVICE_DATE.getCode())) {
                return GlobalEasyGoReasonCode.OUT_OF_SERVICE_DATE.toString();
            } else if (subReasonCode.getSub_code().startsWith(ReasonCode.POLICY_NOT_EXIST.getCode())) {
                return GlobalEasyGoReasonCode.POLICY_NOT_EXIST.toString();
            } else if (subReasonCode.getSub_code().startsWith(ReasonCode.RULE_ENGINE_TIMEOUT.getCode())) {
                return GlobalEasyGoReasonCode.POLICY_EXECUTE_TIMEOUT.toString();
            } else if (subReasonCode.getSub_code().startsWith(ReasonCode.NO_RESULT.getCode())) {
                return GlobalEasyGoReasonCode.NO_RESULT.toString();
            } else {
                return GlobalEasyGoReasonCode.INTERNAL_ERROR.toString();
            }
        }
        return null;
    }
}
