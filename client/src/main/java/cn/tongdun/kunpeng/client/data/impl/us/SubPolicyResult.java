package cn.tongdun.kunpeng.client.data.impl.us;

import cn.tongdun.kunpeng.client.data.IHitRule;
import cn.tongdun.kunpeng.client.data.ISubPolicyResult;
import cn.tongdun.kunpeng.client.data.PolicyMode;
import cn.tongdun.kunpeng.client.data.impl.camel.HitRule;

import java.util.List;

/**
 * @author: yuanhang
 * @date: 2020-06-17 13:53
 **/
public class SubPolicyResult implements ISubPolicyResult {

    private String policyUuid;

    private String policyName;

    private Integer policyScore;

    private String riskType;

    private List<HitRule> hitRules;

    private List<HitRule> hitTestRules;

    private String policyMode;

    private String dealType;

    private List<HitRule> immunoRuleList;

    @Override
    public String getSubPolicyUuid() {
        return null;
    }

    @Override
    public void setSubPolicyUuid(String subPolicyUuid) {

    }

    @Override
    public String getSubPolicyName() {
        return null;
    }

    @Override
    public void setSubPolicyName(String subPolicyName) {

    }

    @Override
    public String getPolicyDecision() {
        return null;
    }

    @Override
    public void setPolicyDecision(String policyDecision) {

    }

    @Override
    public int getPolicyScore() {
        return 0;
    }

    @Override
    public void setPolicyScore(int policyScore) {

    }

    @Override
    public String getRiskType() {
        return null;
    }

    @Override
    public void setRiskType(String riskType) {

    }

    @Override
    public List<IHitRule> getHitRules() {
        return null;
    }

    @Override
    public void setHitRules(List<IHitRule> hitRules) {

    }

    @Override
    public PolicyMode getPolicyMode() {
        return null;
    }

    @Override
    public void setPolicyMode(PolicyMode policyMode) {

    }
}
