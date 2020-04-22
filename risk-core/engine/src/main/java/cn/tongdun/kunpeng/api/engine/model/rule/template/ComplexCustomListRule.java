package cn.tongdun.kunpeng.api.engine.model.rule.template;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.EvalResult;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.rule.AbstractRule;
import cn.fraudmetrix.module.tdrule.util.FunctionLoader;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.ComplexCustomListFunction;

/**
 * @Author: liuq
 * @Date: 2020/2/26 7:15 PM
 */
public class ComplexCustomListRule extends AbstractRule {

    private ComplexCustomListFunction complexCustomListFunction;

    @Override
    public EvalResult run(ExecuteContext executeContext) {
        return EvalResult.valueOf(complexCustomListFunction.eval(executeContext));
    }

    @Override
    public void parse(RawRule rawRule) {
        if (null == rawRule || rawRule.getFunctionDescList() == null || rawRule.getFunctionDescList().isEmpty()) {
            throw new ParseException("LocalRegexRule parse error!null == rawRule or rawRule.getFunctionDescList is blank!");
        }
        if (rawRule.getFunctionDescList().size() > 1) {
            throw new ParseException("LocalRegexRule parse error!expect 1 FunctionDesc,but input :" + rawRule.getFunctionDescList().size());
        }
        complexCustomListFunction = (ComplexCustomListFunction) FunctionLoader.getFunction(rawRule.getFunctionDescList().get(0));
    }
}
