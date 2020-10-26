package cn.tongdun.kunpeng.api.application.output.ext;

import cn.fraudmetrix.module.tdrule.model.IDetail;
import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;
import cn.fraudmetrix.module.tdrule.rule.PlatformIndexDetail;
import cn.fraudmetrix.module.tdrule.rule.PolicyIndexDetail;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.common.data.*;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.api.ruledetail.PlatformIndexCustomDetail;
import cn.tongdun.kunpeng.api.ruledetail.RuleDetail;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.tdframework.core.extension.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 详情输出扩展点默认实现
 *
 * @Author: liang.chen
 * @Date: 2020/2/10 下午9:44
 */
@Extension(tenant = BizScenario.DEFAULT, business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class DefaultRuleDetailOutputExt implements IRuleDetailOutputExtPt {


    @Autowired
    private DecisionResultTypeCache decisionResultTypeCache;

    @Override
    public boolean ruleDetailOutput(AbstractFraudContext context, IRiskResponse response) {

        Map<String, Map<String, DetailCallable>> functionHitDetail = context.getFunctionHitDetail();
        if (functionHitDetail == null || functionHitDetail.isEmpty()) {
            return true;
        }

        Map<String, RuleResponse> ruleResponseMap = new HashMap<>(16);
        PolicyResponse policyResponse = context.getPolicyResponse();
        List<SubPolicyResponse> subPolicyResponseList = policyResponse.getSubPolicyResponses();
        if (subPolicyResponseList != null) {
            for (SubPolicyResponse subPolicyResponse : subPolicyResponseList) {
                List<RuleResponse> ruleResponseList = subPolicyResponse.getRuleResponses();
                if (ruleResponseList != null) {
                    for (RuleResponse ruleResponse : ruleResponseList) {
                        ruleResponseMap.put(ruleResponse.getUuid(), ruleResponse);
                    }
                }
            }
        }

        List<RuleDetail> ruleDetailList = new ArrayList<>();

        for (Map.Entry<String, Map<String, DetailCallable>> ruleEntry : functionHitDetail.entrySet()) {
            RuleDetail ruleDetail = new RuleDetail();
            String ruleUuid = ruleEntry.getKey();
            ruleDetail.setRuleUuid(ruleUuid);
            RuleResponse ruleResponse = ruleResponseMap.get(ruleUuid);
            if (ruleResponse != null) {
                //非权重模式
                if (ruleResponse.getDecision() != null) {
                    ruleDetail.setDecision(ruleResponse.getDecision());
                }
                //权重模式下取分数
                if (ruleResponse.getScore() != null) {
                    ruleDetail.setScore((double) ruleResponse.getScore());
                }
                ruleDetail.setRuleName(ruleResponse.getName());
            }
            List<ConditionDetail> conditions = new ArrayList<>();
            for (Map.Entry<String, DetailCallable> conditionEntry : ruleEntry.getValue().entrySet()) {
                IDetail iDetail = conditionEntry.getValue().call();

                // 处理平台指标/策略指标 相关详情输出
                if (iDetail instanceof PlatformIndexDetail || iDetail instanceof PolicyIndexDetail) {
                    PlatformIndexCustomDetail resultDetail = new PlatformIndexCustomDetail();
                    String indexId = iDetail instanceof PlatformIndexDetail ? ((PlatformIndexDetail) iDetail).getIndexId() : ((PolicyIndexDetail)iDetail).getPolicyIndexId();
                    PlatformIndexData platformIndexData = context.getPlatformIndexMap().get(indexId);
                    cn.tongdun.gaea.paas.dto.ConditionDetail indexDataDetail = (cn.tongdun.gaea.paas.dto.ConditionDetail) platformIndexData.getDetail();
                    if (platformIndexData != null) {
                        resultDetail.setConditionUuid(conditionEntry.getKey());
                        resultDetail.setIndexDesc(indexDataDetail.getDesc());
                        resultDetail.setIndexName(indexDataDetail.getName());
                        resultDetail.setIndexResult(indexDataDetail.getResult());
                        resultDetail.setIndexDim(String.join(",", indexDataDetail.getMasterDimSet()));
                    }
                    conditions.add(resultDetail);
                } else {
                    conditions.add((ConditionDetail) iDetail);
                }
            }
            if (!conditions.isEmpty()) {
                ruleDetail.setConditions(conditions);
            }
            ruleDetailList.add(ruleDetail);
        }

        if (!ruleDetailList.isEmpty()) {
            response.setRuleDetails(ruleDetailList);
        }

        return true;
    }
}
