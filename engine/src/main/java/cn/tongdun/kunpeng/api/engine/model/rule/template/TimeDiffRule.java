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
        if (null == rawRule || rawRule.getFunctionDescList() == null || rawRule.getFunctionDescList().isEmpty()) {
            throw new ParseException("TimeDiffRule parse error!null == rawRule or rawRule.getFunctionDescList is blank!");
        }
        if (rawRule.getFunctionDescList().size() > 1) {
            throw new ParseException("TimeDiffRule parse error!expect 1 FunctionDesc,but input :" + rawRule.getFunctionDescList().size());
        }
        FunctionDesc functionDesc = rawRule.getFunctionDescList().get(0);
        Function function = FunctionLoader.getFunction(functionDesc);

        String timeOperator = null;
        Double timeslice = null;

        for (FunctionParam functionParam : functionDesc.getParamList()) {
            if (StringUtils.equalsIgnoreCase(functionParam.getName(), "timeOperator")) {
                timeOperator = functionParam.getValue();
            } else if (StringUtils.equalsIgnoreCase(functionParam.getName(), "timeslice")) {
                timeslice = Double.parseDouble(functionParam.getValue());
            }
        }
        operator = (AbstractBinaryOperator) OperatorLoader.getOperator(TdRuleOperatorMapUtils.getEngineTypeFromDisplay(timeOperator));
        if (null == operator) {
            throw new ParseException("TimeDiffRule parse error,rawRule id:" + rawRule.getId() + ",timeOperator:" + timeOperator);
        }
        if (timeslice == null || timeslice.isNaN()) {
            throw new ParseException("TimeDiffRule parse error,rawRule id:" + rawRule.getId() + ",timeslice:" + timeslice);
        }
        Variable right = new Literal(timeslice, null);
        operator.addOperand(function);
        operator.addOperand(right);

    }
}
