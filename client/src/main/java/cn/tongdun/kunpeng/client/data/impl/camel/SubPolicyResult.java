package cn.tongdun.kunpeng.client.data.impl.camel;

import cn.tongdun.kunpeng.client.data.IHitRule;
import cn.tongdun.kunpeng.client.data.ISubPolicyResult;
import cn.tongdun.kunpeng.client.data.PolicyMode;
import lombok.Data;

import java.util.List;

@Data
public class SubPolicyResult implements ISubPolicyResult {

    private static final long serialVersionUID = -965304098400137796L;

    /**
     * 策略UUID
     */
    private String            subPolicyUuid;
    /**
     * 策略名称
     */
    private String            subPolicyName;
    /**
     * 策略结果
     */
    private String            policyDecision;
    /**
     * 风险分数
     */
    private int               policyScore;
    /**
     * 风险类型
     */
    private String            riskType;
    /**
     * 命中规则
     */
    private List<IHitRule>     hitRules;

    private PolicyMode policyMode;

    @Override
    public String getSubPolicyUuid() {
        return subPolicyUuid;
    }

    @Override
    public void setSubPolicyUuid(String subPolicyUuid) {
        this.subPolicyUuid = subPolicyUuid;
    }

    @Override
    public String getSubPolicyName() {
        return subPolicyName;
    }

    @Override
    public void setSubPolicyName(String subPolicyName) {
        this.subPolicyName = subPolicyName;
    }

    @Override
    public String getPolicyDecision() {
        return policyDecision;
    }

    @Override
    public void setPolicyDecision(String policyDecision) {
        this.policyDecision = policyDecision;
    }

    @Override
    public int getPolicyScore() {
        return policyScore;
    }

    @Override
    public void setPolicyScore(int policyScore) {
        this.policyScore = policyScore;
    }

    @Override
    public String getRiskType() {
        return riskType;
    }

    @Override
    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    @Override
    public List<IHitRule> getHitRules() {
        return hitRules;
    }

    @Override
    public void setHitRules(List<IHitRule> hitRules) {
        this.hitRules = hitRules;
    }

    @Override
    public PolicyMode getPolicyMode() {
        return policyMode;
    }

    @Override
    public void setPolicyMode(PolicyMode policyMode) {
        this.policyMode = policyMode;
    }

    @Override
    public String getDealType() {
        return null;
    }

    @Override
    public void setDealType(String dealType) {

    }
}
