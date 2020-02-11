package cn.tongdun.kunpeng.api.engine.model.rule.template;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.EvalResult;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.Function;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.rule.AbstractRule;
import cn.fraudmetrix.module.tdrule.util.FunctionLoader;

/**
 * @Author: liuq
 * @Date: 2019/12/6 8:34 PM
 */
public class LocalRegexRule extends AbstractRule {

    private Function function;

    @Override
    public EvalResult run(ExecuteContext executeContext) {
        Object ret = function.eval(executeContext);
        return EvalResult.valueOf(ret);
    }

    @Override
    public void parse(RawRule rawRule) {

        if (null == rawRule || rawRule.getFunctionDescList() == null || rawRule.getFunctionDescList().isEmpty()) {
            throw new ParseException("LocalRegexRule parse error!null == rawRule or rawRule.getFunctionDescList is blank!");
        }
        if (rawRule.getFunctionDescList().size() > 1) {
            throw new ParseException("LocalRegexRule parse error!expect 1 FunctionDesc,but input :" + rawRule.getFunctionDescList().size());
        }
        function = FunctionLoader.getFunction(rawRule.getFunctionDescList().get(0));
    }
}
