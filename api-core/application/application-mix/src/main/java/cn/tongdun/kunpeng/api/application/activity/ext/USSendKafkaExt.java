package cn.tongdun.kunpeng.api.application.activity.ext;

import cn.tongdun.kunpeng.api.application.activity.common.ActivityStoreKafkaWorker;
import cn.tongdun.kunpeng.api.application.step.ext.ISendKafkaExtPt;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.QueueItem;
import cn.tongdun.kunpeng.client.data.IHitRule;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.ISubPolicyResult;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.client.data.impl.camel.HitRule;
import cn.tongdun.kunpeng.client.data.impl.camel.RiskResponse;
import cn.tongdun.kunpeng.client.data.impl.camel.SubPolicyResult;
import cn.tongdun.kunpeng.client.data.impl.us.PolicyResult;
import cn.tongdun.tdframework.core.extension.Extension;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: yuanhang
 * @date: 2020-07-28 18:09
 **/
@Extension(business = BizScenario.DEFAULT, tenant = "us", partner = "globalegrow")
public class USSendKafkaExt implements ISendKafkaExtPt {

    @Autowired
    private ActivityStoreKafkaWorker activityStoreKafkaWorker;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {

        RiskResponse res = new RiskResponse();
        if (response.isSuccess() &&  null != response.getPolicyDetailResult()) {
            PolicyResult policyResult = (PolicyResult) response.getPolicyDetailResult();
            res.setFinalScore(policyResult.getFinalScore());
            res.setPolicyName(policyResult.getPolicyName());
            res.setPolicyUuid(policyResult.getPolicyUuid());
            res.setSuccess(policyResult.isSuccess());
            res.setFinalScore(policyResult.getFinalScore());
            res.setFinalDecision(policyResult.getFinalDealType());
            res.setSeqId(policyResult.getSeqId());

            List<ISubPolicyResult> subPolicies = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(policyResult.getPolicySet())) {
                for (int var0 = 0; var0 < policyResult.getPolicySet().size(); var0++) {
                    cn.tongdun.kunpeng.client.data.impl.us.SubPolicyResult subPolicyResult = (cn.tongdun.kunpeng.client.data.impl.us.SubPolicyResult) policyResult.getPolicySet().get(var0);
                    SubPolicyResult subRes = new SubPolicyResult();
                    if (null != subPolicyResult && CollectionUtils.isNotEmpty(subPolicyResult.getHitRules())) {
                        List<IHitRule> hitRules = subPolicyResult.getHitRules().stream().map(r -> {
                            HitRule hitRule = new HitRule();
                            hitRule.setScore(r.getScore());
                            hitRule.setName(r.getName());
                            hitRule.setUuid(r.getUuid());
                            return hitRule;
                        }).collect(Collectors.toList());
                        subRes.setHitRules(CollectionUtils.isEmpty(hitRules) ? null : hitRules);
                        subRes.setPolicyScore(subPolicyResult.getPolicyScore());
                        subRes.setRiskType(subPolicyResult.getRiskType());
                        subRes.setPolicyDecision(subPolicyResult.getDealType());
                        subRes.setPolicyMode(subPolicyResult.getPolicyMode());
                        subRes.setSubPolicyUuid(subPolicyResult.getSubPolicyUuid());
                        subRes.setSubPolicyName(subPolicyResult.getSubPolicyName());
                        subPolicies.add(subRes);
                    }
                }
            }
            res.setSubPolicys(subPolicies);
            res.setRuleDetails(response.getRuleDetails());
        } else {
            res.setSuccess(false);
        }

        QueueItem queueItem = new QueueItem(context, res, request);

        if (activityStoreKafkaWorker.getFilter().test(queueItem)) {
            activityStoreKafkaWorker.onEvent(queueItem);
        }
        return true;
    }
}
