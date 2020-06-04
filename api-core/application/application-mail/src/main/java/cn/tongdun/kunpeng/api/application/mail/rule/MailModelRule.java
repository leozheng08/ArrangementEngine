package cn.tongdun.kunpeng.api.application.mail.rule;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.EvalResult;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.rule.AbstractRule;
import cn.fraudmetrix.module.tdrule.util.FunctionLoader;
import cn.tongdun.kunpeng.api.application.mail.function.MailModelFunction;

/**
 * @author yuanhang
 * @date: 2020-05-27 19:31
 */
public class MailModelRule extends AbstractRule {

    private MailModelFunction function;

    @Override
    public EvalResult run(ExecuteContext executeContext) {
        return EvalResult.valueOf(function.eval(executeContext));
    }

    @Override
    public void parse(RawRule rawRule) {

        if (null == rawRule || rawRule.getFunctionDescList() == null || rawRule.getFunctionDescList().isEmpty()) {
            throw new ParseException("MailModelFunction parse error!null == rawRule or rawRule.getFunctionDescList is blank!");
        }

        if (rawRule.getFunctionDescList().size() > 1) {
            throw new ParseException("MailModelFunction parse error!expect 1 FunctionDesc,but input :" + rawRule.getFunctionDescList().size());
        }

        function = (MailModelFunction) FunctionLoader.getFunction(rawRule.getFunctionDescList().get(0));
    }

}
