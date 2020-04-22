package cn.tongdun.kunpeng.api.engine.model.constant;

/**
 * 规则表rule、规则条件表rule_condition_element中的biz_type
 */
public enum BizTypeEnum {
    SUB_POLICY("子策略"),
    RULE("规则"),
    DECISION_FLOW("决策流");

    private String desc;

    BizTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
