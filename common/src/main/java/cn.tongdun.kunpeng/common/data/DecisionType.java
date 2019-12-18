package cn.tongdun.kunpeng.common.data;

public enum DecisionType {

    DECISION_FLOW("DECISION_FLOW"),
    POLICY_SET("POLICY_SET"),
    DECISION_TABLE("DECISION_TABLE"),
    POLICY_TREE("POLICY_TREE");

    private String identity;

    DecisionType(String identity) {
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }
}
