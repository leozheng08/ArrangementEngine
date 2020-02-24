package cn.tongdun.kunpeng.client.data;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PolicyResult implements Serializable {

    private static final long serialVersionUID = -965304098400137796L;

    /**
     * 策略UUID
     */
    private String            policy_uuid;
    /**
     * 策略名称
     */
    private String            policy_name;
    /**
     * 策略结果
     */
    private String            policy_decision;
    /**
     * 风险分数
     */
    private int               policy_score;
    /**
     * 风险类型
     */
    private String            risk_type;
    /**
     * 命中规则
     */
    private List<HitRule>     hit_rules;

    private PolicyMode        policy_mode;

    public String getPolicy_uuid() {
        return policy_uuid;
    }

    public void setPolicy_uuid(String policy_uuid) {
        this.policy_uuid = policy_uuid;
    }

    public String getPolicy_name() {
        return policy_name;
    }

    public void setPolicy_name(String policy_name) {
        this.policy_name = policy_name;
    }

    public String getPolicy_decision() {
        return policy_decision;
    }

    public void setPolicy_decision(String policy_decision) {
        this.policy_decision = policy_decision;
    }

    public int getPolicy_score() {
        return policy_score;
    }

    public void setPolicy_score(int policy_score) {
        this.policy_score = policy_score;
    }

    public String getRisk_type() {
        return risk_type;
    }

    public void setRisk_type(String risk_type) {
        this.risk_type = risk_type;
    }

    public List<HitRule> getHit_rules() {
        return hit_rules;
    }

    public void setHit_rules(List<HitRule> hit_rules) {
        this.hit_rules = hit_rules;
    }

    public PolicyMode getPolicy_mode() {
        return policy_mode;
    }

    public void setPolicy_mode(PolicyMode policy_mode) {
        this.policy_mode = policy_mode;
    }

}
