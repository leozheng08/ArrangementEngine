package cn.tongdun.kunpeng.client.data.impl.us;

import cn.tongdun.kunpeng.client.data.IHitRule;
import cn.tongdun.kunpeng.client.data.ISubPolicyResult;
import cn.tongdun.kunpeng.client.data.PolicyMode;
import com.google.common.collect.Lists;

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

    private List<IHitRule> hitRules;

    private List<IHitRule> hitTestRules;

    private PolicyMode policyMode;

    private String dealType;

    private List<IHitRule> immunoRuleList;

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public void setPolicyScore(Integer policyScore) {
        this.policyScore = policyScore;
    }

    public List<IHitRule> getHitTestRules() {
        return Lists.newArrayList();
    }

    public void setHitTestRules(List<IHitRule> hitTestRules) {
        this.hitTestRules = hitTestRules;
    }

    @Override
    public String getDealType() {
        return dealType;
    }

    @Override
    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public List<IHitRule> getImmunoRuleList() {
        return Lists.newArrayList();
    }

    public void setImmunoRuleList(List<IHitRule> immunoRuleList) {
        this.immunoRuleList = immunoRuleList;
    }

    @Override
    public String getSubPolicyUuid() {
        return this.policyUuid;
    }

    @Override
    public void setSubPolicyUuid(String subPolicyUuid) {
        this.policyUuid = subPolicyUuid;
    }

    @Override
    public String getSubPolicyName() {
        return this.policyName;
    }

    @Override
    public void setSubPolicyName(String subPolicyName) {
        this.policyName = subPolicyName;
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
        return this.policyScore;
    }

    @Override
    public void setPolicyScore(int policyScore) {
        this.policyScore = policyScore;
    }

    @Override
    public String getRiskType() {
        return this.riskType;
    }

    @Override
    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    @Override
    public List<IHitRule> getHitRules() {
        return this.hitRules;
    }

    @Override
    public void setHitRules(List<IHitRule> hitRules) {
        this.hitRules = hitRules;
    }

    @Override
    public PolicyMode getPolicyMode() {
        return this.policyMode;
    }

    @Override
    public void setPolicyMode(PolicyMode policyMode) {
        this.policyMode = policyMode;
    }
}
