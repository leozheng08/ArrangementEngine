package cn.tongdun.kunpeng.api.engine.model.decisionmode;

public enum DecisionModeType {

    FLOW("决策流"),
    TABLE("决策表"),
    TREE("决策树"),
    DEFAULT("并行执行子策略");

    private String desc;

    DecisionModeType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
