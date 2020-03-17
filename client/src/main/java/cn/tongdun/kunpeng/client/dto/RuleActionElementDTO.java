package cn.tongdun.kunpeng.client.dto;


import lombok.Data;

/**
 * 规则操作
 */
@Data
public class RuleActionElementDTO extends CommonDTO {

    private static final long serialVersionUID = 7768242359914265185L;


    /**
     * 规则uuid rule_uuid
     */
    private String ruleUuid;

    /**
     * 描述 description
     */
    private String description;

    /**
     * 规则动作类型 assign:赋值动作 addCustomList:自定义列表 action_type
     */
    private String actionType;

    /**
     * 动作 赋值：[{“leftProperty":"accountLogin","leftPropertyType":"","operator":"==","rightValue":"444422","rightValueType":"input"}]  addCustomList： [  {  "field":"deviceId",  "customListUuid":"9997467d26644ff99f8b5dd73ce247b9",  "timeslice":"3",  "timeunit":"d"  } ]  actions
     */
    private String actions;

}
