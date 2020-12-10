package cn.tongdun.kunpeng.api.application.output.ext;

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
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.shenwei.dto.ShenWeiConditionDetail;
import cn.tongdun.tdframework.core.extension.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 详情输出扩展点默认实现
 *
 * @Author: liang.chen
 * @Date: 2020/2/10 下午9:44
 */
@Extension(tenant = BizScenario.DEFAULT, business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class DefaultRuleDetailOutputExt implements IRuleDetailOutputExtPt {


    @Autowired
    RuleCache ruleCache;

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
            Set<ConditionDetail> conditions = new HashSet<>();
            for (Map.Entry<String, DetailCallable> conditionEntry : ruleEntry.getValue().entrySet()) {
                IDetail iDetail = conditionEntry.getValue().call();

                // 处理平台指标/策略指标 相关详情输出
                if (iDetail instanceof PlatformIndexDetail) {
                    IndexCustomDetail resultDetail = new IndexCustomDetail();
                    String indexId = ((PlatformIndexDetail) iDetail).getIndexId();
                    PlatformIndexData platformIndexData = context.getPlatformIndexMap().get(indexId);
                    ShenWeiConditionDetail indexDataDetail = (ShenWeiConditionDetail) platformIndexData.getDetail();
                    if (platformIndexData != null) {
                        resultDetail.setConditionUuid(conditionEntry.getKey());
                        resultDetail.setIndexDesc(indexDataDetail.getDesc());
                        if (StringUtils.isNotEmpty(((PlatformIndexDetail) iDetail).getDescription())) {
                            resultDetail.setIndexDesc(((PlatformIndexDetail) iDetail).getDescription());
                        }
                        resultDetail.setIndexName(indexDataDetail.getName());
                        resultDetail.setIndexResult(indexDataDetail.getResult());
                        Set<String> dimSets = indexDataDetail.getMasterDimSet();
                        if (CollectionUtils.isNotEmpty(dimSets)) {
                            Set<String> dimVal = new HashSet<>(dimSets.size());
                            for (String dim : dimSets) {
                                dimVal.add(String.valueOf(context.get(dim)));
                            }
                            resultDetail.setIndexDim(String.join(",", dimVal));
                        }
                    }
                    conditions.add(resultDetail);
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

        return true;
    }
}
