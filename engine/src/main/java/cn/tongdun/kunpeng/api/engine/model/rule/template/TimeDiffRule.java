package cn.tongdun.kunpeng.api.engine.model.rule.template;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.EvalResult;
import cn.fraudmetrix.module.tdrule.eval.Literal;
import cn.fraudmetrix.module.tdrule.eval.Variable;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.Function;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.operator.AbstractBinaryOperator;
import cn.fraudmetrix.module.tdrule.rule.AbstractRule;
import cn.fraudmetrix.module.tdrule.util.FunctionLoader;
import cn.fraudmetrix.module.tdrule.util.OperatorLoader;
import cn.tongdun.kunpeng.api.engine.util.RawRuleParseUtils;
import cn.tongdun.kunpeng.api.engine.util.TdRuleOperatorMapUtils;
import org.apache.commons.lang3.StringUtils;

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
