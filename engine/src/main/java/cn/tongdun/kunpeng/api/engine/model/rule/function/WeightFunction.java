package cn.tongdun.kunpeng.api.engine.model.rule.function;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.tongdun.kunpeng.api.engine.model.rule.function.pattern.AbstractCalculateFunction;

/**
 * @Author: liuq
 * @Date: 2020/2/12 5:27 PM
 */
public class WeightFunction extends AbstractCalculateFunction {

    @Override
    public String getName() {
        return "ruleWeight";
    }

    @Override
    protected FunctionResult run(ExecuteContext executeContext) {
        return null;
    }

    @Override
    protected void parseFunction(FunctionDesc functionDesc) {

    }
}
