package cn.tongdun.kunpeng.api.engine.model.rule.action;

import cn.fraudmetrix.module.tdrule.action.Action;
import cn.fraudmetrix.module.tdrule.action.ActionDesc;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.*;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import com.alibaba.fastjson.JSONObject;

/**
 * @Author: liuq
 * @Date: 2019/12/5 2:34 PM
 */
public class AssignmentAction implements Action {

    private String left;
    private Variable right;

    @Override
    public void parse(ActionDesc actionDesc) {
        //{"leftProperty":{"name":"idNumber","type":"context"},"op":"=","rightValue":{"type":"input","value":"333333"}}
        JSONObject json = JSONObject.parseObject(actionDesc.getParams());
        JSONObject leftProperty = json.getJSONObject("leftProperty");
        left=leftProperty.getString("name");

        JSONObject rightValueJson = json.getJSONObject("rightValue");
        String rightValueType = rightValueJson.getString("type");
        String rightValue=rightValueJson.getString("value");

        switch (rightValueType) {
            case "input":
                right = new Literal(rightValue);
                break;
            case "context":
                right = new Field(rightValue, "object");
                break;
            default:
                throw new ParseException("AssignmentAction parse error!");
        }


    }

    @Override
    public Void eval(ExecuteContext executeContext) {
        Object value = right.eval(executeContext);
        if (value != null) {
            executeContext.setField(left, value);
        }
        return null;
    }
}
