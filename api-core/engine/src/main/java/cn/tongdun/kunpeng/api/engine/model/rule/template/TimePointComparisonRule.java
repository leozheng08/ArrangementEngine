package cn.tongdun.kunpeng.api.engine.model.rule.template;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.EvalResult;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.rule.AbstractRule;
import cn.fraudmetrix.module.tdrule.util.FunctionLoader;
import cn.tongdun.kunpeng.api.engine.model.rule.function.time.TimePointFunction;

public class TimePointComparisonRule extends AbstractRule {

    private TimePointFunction function;


    @Override
    public EvalResult run(ExecuteContext executeContext) {
        return EvalResult.valueOf(function.eval(executeContext));
    }

    @Override
    public void parse(RawRule rawRule) {
        if (null == rawRule || rawRule.getFunctionDescList() == null || rawRule.getFunctionDescList().isEmpty()) {
            throw new ParseException("GpsDistanceRule parse error!null == rawRule or rawRule.getFunctionDescList is blank!");
        }
        if (rawRule.getFunctionDescList().size() > 1) {
            throw new ParseException("GpsDistanceRule parse error!expect 1 FunctionDesc,but input :" + rawRule.getFunctionDescList().size());
        }

        FunctionDesc functionDesc = rawRule.getFunctionDescList().get(0);
        function = (TimePointFunction) FunctionLoader.getFunction(functionDesc);

    }
}
