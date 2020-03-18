package cn.tongdun.kunpeng.api.engine.model.rule.template;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.EvalResult;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.operator.AbstractBinaryOperator;
import cn.fraudmetrix.module.tdrule.rule.AbstractRule;
import cn.tongdun.kunpeng.api.engine.util.RawRuleParseUtils;

public class TimeDiffRule extends AbstractRule {

    private AbstractBinaryOperator operator;


    @Override
    public EvalResult run(ExecuteContext executeContext) {
        return operator.eval(executeContext);
    }

    @Override
    public void parse(RawRule rawRule) {
        this.operator = RawRuleParseUtils.parseWithFunctionAndRight(rawRule);

    }
}
