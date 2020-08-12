package cn.tongdun.kunpeng.api.basedata.rule.template.android;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.EvalResult;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.rule.AbstractRule;
import cn.fraudmetrix.module.tdrule.util.FunctionLoader;
import cn.tongdun.kunpeng.api.basedata.rule.function.android.AndroidFpFetchExceptionFunction;

/**
 * @author: yuanhang
 * @date: 2020-08-07 13:37
 **/
public class AndroidFpExceptionRule extends AbstractRule {

    private AndroidFpFetchExceptionFunction function;

    @Override
    public EvalResult run(ExecuteContext executeContext) {
        Object ret = function.eval(executeContext);
        return EvalResult.valueOf(ret);
    }

    @Override
    public void parse(RawRule rawRule) {
        if (null == rawRule || rawRule.getFunctionDescList() == null || rawRule.getFunctionDescList().isEmpty()) {
            throw new ParseException("AndroidFpFetchExceptionRule parse error!null == rawRule or rawRule.getFunctionDescList is blank!");
        }
        if (rawRule.getFunctionDescList().size() > 1) {
            throw new ParseException("AndroidFpFetchExceptionRule parse error!expect 1 FunctionDesc,but input :" + rawRule.getFunctionDescList().size());
        }

        function = (AndroidFpFetchExceptionFunction) FunctionLoader.getFunction(rawRule.getFunctionDescList().get(0));
    }
}