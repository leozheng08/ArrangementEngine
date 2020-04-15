package cn.tongdun.kunpeng.client.data.impl.underline;

import cn.tongdun.kunpeng.client.data.IHitRule;
import cn.tongdun.kunpeng.client.data.ISubPolicyResult;
import cn.tongdun.kunpeng.client.data.PolicyMode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SubPolicyResult implements ISubPolicyResult {

    private static final long serialVersionUID = -965304098400137796L;

    /**
     * 策略UUID
     */
    @JsonProperty("policy_uuid")
    private String            subPolicyUuid;
    /**
     * 策略名称
     */
    @JsonProperty("policy_name")
    private String            subPolicyName;
    /**
     * 策略结果
     */
    @JsonProperty("policy_decision")
    private String            policyDecision;
    /**
     * 风险分数
     */
    @JsonProperty("policy_score")
    private int               policyScore;
    /**
     * 风险类型
     */
    @JsonProperty("risk_type")
    private String            riskType;
    /**
     * 命中规则
     */
    @JsonProperty("hit_rules")
    private List<IHitRule>     hitRules;

    @JsonProperty("policy_mode")
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
}
