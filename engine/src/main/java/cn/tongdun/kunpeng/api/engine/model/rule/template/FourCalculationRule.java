package cn.tongdun.kunpeng.api.engine.model.rule.template;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.EvalResult;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.rule.AbstractRule;
import cn.fraudmetrix.module.tdrule.rule.EvalDetailResult;

/**
 * @Author: liuq
 * @Date: 2020/2/12 11:30 AM
 */
public class FourCalculationRule extends AbstractRule {

    @Override
    public EvalResult run(ExecuteContext executeContext) {
        return null;
    }

    @Override
    public void parse(RawRule rawRule) {
        if (null == rawRule || rawRule.getFunctionDescList() == null || rawRule.getFunctionDescList().isEmpty()) {
            throw new ParseException("LocalRegexRule parse error!null == rawRule or rawRule.getFunctionDescList is blank!");
        }

    }
}
