package cn.tongdun.kunpeng.api.engine.model.rule.action;

import cn.fraudmetrix.module.tdrule.action.Action;
import cn.fraudmetrix.module.tdrule.action.ActionDesc;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.*;
import com.alibaba.fastjson.JSONObject;

/**
 * @Author: liuq
 * @Date: 2019/12/5 2:34 PM
 */
public class AssignmentAction implements Action {

    private String leftProperty;
    private Variable right;

    @Override
    public void parse(ActionDesc actionDesc) {
        //{"leftProperty":"accountLogin","leftPropertyType":"","operator":"==","rightValue":"abc","rightValueType":"input"}
        JSONObject json = JSONObject.parseObject(actionDesc.getParams());
        leftProperty = json.getString("leftProperty");

        String rightValueType = json.getString("rightValueType");
        String rightValue = json.getString("rightValue");
        switch (rightValueType){
            case "input":
                right = new Literal(rightValue);
                break;
            case "context":
                right = new Field(rightValue,"object");
                break;
        }


    }

    @Override
    public Void eval(ExecuteContext executeContext) {
        Object value = right.eval(executeContext);
        if(value != null) {
            executeContext.setField(leftProperty, value);
        }
        return null;
    }
}
