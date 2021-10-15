package cn.tongdun.kunpeng.api.application.output.ext;
import cn.fraudmetrix.module.ruledetail.AssociationPartnerDetail;
import cn.fraudmetrix.module.tdrule.model.IDetail;
import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;
import cn.fraudmetrix.module.tdrule.rule.FieldDetail;
import cn.fraudmetrix.module.tdrule.rule.PlatformIndexDetail;
import cn.fraudmetrix.module.tdrule.rule.PolicyIndexDetail;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.common.data.*;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.ruledetail.FieldCustomDetail;
import cn.tongdun.kunpeng.api.ruledetail.IndexCustomDetail;
import cn.tongdun.kunpeng.api.ruledetail.RuleDetail;
import cn.tongdun.kunpeng.client.data.*;
import cn.tongdun.shenwei.dto.ShenWeiConditionDetail;
import cn.tongdun.tdframework.core.extension.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author hls
 * @version 1.0
 * @date 2021/10/11 2:32 下午
 */
@Extension(tenant = "cs", business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class CSRuleDetailOutputExt implements IRuleDetailOutputExtPt {

    @Autowired
    RuleCache ruleCache;

    @Override
    public boolean ruleDetailOutput(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {

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
            Set<ConditionDetail> conditions = new HashSet<>();
            for (Map.Entry<String, DetailCallable> conditionEntry : ruleEntry.getValue().entrySet()) {
                IDetail iDetail = conditionEntry.getValue().call();

                // 处理平台指标/策略指标 相关详情输出
                if (iDetail instanceof PlatformIndexDetail) {
                    IndexCustomDetail resultDetail = new IndexCustomDetail();
                    String indexId = ((PlatformIndexDetail) iDetail).getIndexId();
                    PlatformIndexData platformIndexData = context.getPlatformIndexMap().get(indexId);
                    if (platformIndexData != null) {
                        resultDetail.setConditionUuid(conditionEntry.getKey());
                        // resultDetail.setIndexDesc(indexDataDetail.getDesc());
                        // 有条件描述的情况下，desc添加条件描述，不添加指标描述
                        if (StringUtils.isNotEmpty(((PlatformIndexDetail) iDetail).getDescription())) {
                            resultDetail.setIndexDesc(((PlatformIndexDetail) iDetail).getDescription());
                        }
                        resultDetail.setIndexName(platformIndexData.getDesc());
                        resultDetail.setIndexResult(platformIndexData.getValue());
                        conditions.add(resultDetail);
                    }
                } else if (iDetail instanceof PolicyIndexDetail) {
                    IndexCustomDetail resultDetail = new IndexCustomDetail();
                    String indexId = ((PolicyIndexDetail) iDetail).getPolicyIndexId();
                    Double indexVal = context.getPolicyIndexMap().get(indexId);
                    resultDetail.setIndexResult(indexVal);
                    conditions.add(resultDetail);
                } else if (iDetail instanceof FieldDetail) {
                    FieldDetail fieldDetail = (FieldDetail) iDetail;
                    FieldCustomDetail fieldCustomDetail = new FieldCustomDetail();
                    fieldCustomDetail.setValue(fieldDetail.getValue());

                    IFieldDefinition fieldDefinition = context.getSystemFieldMap().get(fieldDetail.getName());
                    if (null == fieldDefinition) {
                        fieldDefinition = context.getExtendFieldMap().get(fieldDetail.getName());
                    }
                    if (null != fieldDefinition) {
                        fieldCustomDetail.setName(fieldDefinition.getDisplayName());
                    }
                    conditions.add(fieldCustomDetail);
                } else {
                    conditions.add((ConditionDetail) iDetail);

                }
            }
            if (!conditions.isEmpty()) {
                ruleDetail.setConditions(Lists.newArrayList(conditions));
            }
            ruleDetailList.add(ruleDetail);
        }

        if (!ruleDetailList.isEmpty()) {
            response.setRuleDetails(ruleDetailList);
        }

        if (StringUtils.contains(request.getRespDetailType(), "hitRuleTestDetails")) {
            response.setRuleTestDetails(ruleDetailList);
        }
        return true;
    }
}
