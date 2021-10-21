package cn.tongdun.kunpeng.api.engine.model.rule.action;

import cn.fraudmetrix.module.tdrule.action.Action;
import cn.fraudmetrix.module.tdrule.action.ActionDesc;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.*;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liuq
 * @Date: 2019/12/5 2:34 PM
 */
public class AssignmentAction implements Action {

    private String left;
    private Variable right;

    @Override
    public void parse(ActionDesc actionDesc) {
        //[{"leftProperty":"reject3","rightValueType":"input","rightValue":"3","leftPropertyType":"","op":"="}]
        Map json = JSON.parseObject(actionDesc.getParams(), HashMap.class);
        left= JsonUtil.getString(json,("leftProperty"));

        String rightValueType = JsonUtil.getString(json,"rightValueType");
        String rightValue=JsonUtil.getString(json,"rightValue");

        if(StringUtils.isBlank(rightValueType)){
            return;
        }
        switch (rightValueType) {
            case "input":
                right = new Literal(rightValue);
                break;
            case "context":
                right = new Field(rightValue, "object");
                break;
            case "indicatrix":
                right = new PlatformIndex(rightValue, false, null);
                break;
            case "index":
                right = new PolicyIndex(rightValue);
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
