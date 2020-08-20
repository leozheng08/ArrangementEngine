package cn.tongdun.kunpeng.api.engine.model.rule.action;

import cn.fraudmetrix.module.tdrule.action.Action;
import cn.fraudmetrix.module.tdrule.action.ActionDesc;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;

/**
 * @author: yuanhang
 * @date: 2020-08-20 18:19
 **/
public class AddCustomListAction implements Action {

    @Override
    public void parse(ActionDesc actionDesc) {
    }

    @Override
    public Void eval(ExecuteContext executeContext) {
        return null;
    }
}
