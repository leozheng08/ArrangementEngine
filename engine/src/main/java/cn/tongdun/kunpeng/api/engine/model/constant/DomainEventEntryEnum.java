package cn.tongdun.kunpeng.api.engine.model.constant;

import cn.tongdun.kunpeng.share.dataobject.*;

/**
 * @Author: liang.chen
 * @Date: 2020/3/13 下午6:04
 */
public class DOEntryNameEnum {

    policy_definition("policy_definition"),
    policy("policy"),
    sub_policy("sub_policy"),
    rule("rule"),
    decision_flow("decision_flow"),
    index_definition("index_definition"),
    policy_decision_mode("policy_decision_mode"),
    event_type("event_type"),
    dynamic_script("dynamic_script"),
    custom_list_value("custom_list_value"),
    interface_definition("interface_definition"),
    policy_challenger("policy_challenger"),
    custom_list("custom_list"),
    policy_definition("policy_definition");


    private String code;

    DOEntryNameEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


    put("",PolicyDefinitionDO.class);
    put("",PolicyDO.class);
    put("",SubPolicyDO.class);
    put("",RuleDO.class);
    //        put("rule_condition_element", RuleConditionElementDO.class);
//        put("rule_action_element", RuleActionElementDO.class);
    put("",DecisionFlowDO.class);
    put("", IndexDefinitionDO.class);
    put("", PolicyDecisionModeDO.class);
    put("", FieldDefinitionDO.class);
    put("", EventTypeDO.class);
    put("", DynamicScriptDO.class);
    put("", CustomListValueDO.class);
    put("",InterfaceDefinitionDO.class);
    put("",PolicyChallengerDO.class);
}
