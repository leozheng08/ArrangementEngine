package cn.tongdun.kunpeng.api.application.step.ext.response.haiwai;

import cn.tongdun.kunpeng.client.data.IHitRule;
import cn.tongdun.kunpeng.client.data.ISubPolicyResult;
import cn.tongdun.kunpeng.client.data.PolicyMode;
import com.alibaba.fastjson.annotation.JSONField;
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
    @JSONField(name="policy_uuid")
    public String getSubPolicyUuid() {
        return subPolicyUuid;
    }

    @Override
    @JSONField(name="policy_uuid")
    public void setSubPolicyUuid(String subPolicyUuid) {
        this.subPolicyUuid = subPolicyUuid;
    }

    @Override
    @JSONField(name="policy_name")
    public String getSubPolicyName() {
        return subPolicyName;
    }

    @Override
    @JSONField(name="policy_name")
    public void setSubPolicyName(String subPolicyName) {
        this.subPolicyName = subPolicyName;
    }

    @Override
    @JSONField(name="policy_decision")
    public String getPolicyDecision() {
        return policyDecision;
    }

    @Override
    @JSONField(name="policy_decision")
    public void setPolicyDecision(String policyDecision) {
        this.policyDecision = policyDecision;
    }

    @Override
    @JSONField(name="policy_score")
    public int getPolicyScore() {
        return policyScore;
    }

    @Override
    @JSONField(name="policy_score")
    public void setPolicyScore(int policyScore) {
        this.policyScore = policyScore;
    }

    @Override
    @JSONField(name="risk_type")
    public String getRiskType() {
        return riskType;
    }

    @Override
    @JSONField(name="risk_type")
    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    @Override
    @JSONField(name="hit_rules")
    public List<IHitRule> getHitRules() {
        return hitRules;
    }

    @Override
    @JSONField(name="hit_rules")
    public void setHitRules(List<IHitRule> hitRules) {
        this.hitRules = hitRules;
    }

    @Override
    @JSONField(name="policy_mode")
    public PolicyMode getPolicyMode() {
        return policyMode;
    }

    @Override
    @JSONField(name="policy_mode")
    public void setPolicyMode(PolicyMode policyMode) {
        this.policyMode = policyMode;
    }
}
