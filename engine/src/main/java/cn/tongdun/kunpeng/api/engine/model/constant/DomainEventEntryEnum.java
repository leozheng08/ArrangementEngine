package cn.tongdun.kunpeng.api.engine.model.constant;

/**
 * @Author: liang.chen
 * @Date: 2020/3/13 下午6:04
 */
public enum DomainEventEntryEnum {

    POLICY_DEFINITION("策略定义"),
    POLICY("策略"),
    SUB_POLICY("子策略"),
    RULE("规则"),
    DECISION_FLOW("决策流"),
    INDEX_DEFINITION("策略指标"),
    POLICY_DECISION_MODE("决策模式"),
    EVENT_TYPE("事件类型"),
    DYNAMIC_SCRIPT("动态脚本 "),
    INTERFACE_DEFINITION("三方接口定义"),
    POLICY_CHALLENGER("挑战者信息"),
    CUSTOM_LIST("自定义表列"),
    CUSTOM_LIST_VALUE("自定义表列数据");


    private String desc;

    DomainEventEntryEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
