package cn.tongdun.kunpeng.api.engine.model.rule.function.pattern;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.Variable;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.ruledetail.FourCalculationDetail;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FourCalculation extends AbstractCalculateFunction {

    private static final Logger logger = LoggerFactory.getLogger(FourCalculation.class);

    private Variable leftVariableVar;
    private String arithmetic;
    private Variable rightVariableVar;


    @Override
    public String getName() {
        return "pattern/fourCalculation";
    }

    @Override
    public void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("FourCalculation function parse error,no params!");
        }

        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("leftVariable", param.getName())) {
                leftVariableVar = buildVariable(param, "double");
            } else if (StringUtils.equals("arithmetic", param.getName())) {
                arithmetic = param.getValue();
            } else if (StringUtils.equals("rightVariable", param.getName())) {
                rightVariableVar = buildVariable(param, "double");
            }

        });
        if (StringUtils.isBlank(arithmetic)) {
            throw new ParseException("FourCalculation function parse error,arithmetic is blank,conditionUuid:"
                    + functionDesc.getConditionUuid() + ",ruleUuid:" + functionDesc.getRuleUuid());
        }
        if (leftVariableVar == null || rightVariableVar == null) {
            throw new ParseException("FourCalculation function parse error,leftVariableVar == null || rightVariableVar == null,conditionUuid:"
                    + functionDesc.getConditionUuid() + ",ruleUuid:" + functionDesc.getRuleUuid());
        }
    }

    @Override
    public FunctionResult run(ExecuteContext context) {

        Double leftVariable = (Double) leftVariableVar.eval(context);
        Double rightVariable = (Double) rightVariableVar.eval(context);

        if (null == leftVariable || null == rightVariable) {
            logger.warn("left or right is null!");
            return null;
        }

        double result = Double.NaN;
        switch (arithmetic) {
            case "plus":
                result = leftVariable + rightVariable;
                break;
            case "subtract":
                result = leftVariable - rightVariable;
                break;
            case "multiply":
                result = leftVariable * rightVariable;
                break;
            case "divide":
                if (0 != rightVariable) {
                    result = leftVariable / rightVariable;
                } else {
                    logger.warn("divisor can not be zero");
                }
                break;
            default:
                throw new RuntimeException("FourCalculation cann't recognize arithmetic:" + arithmetic);
        }
        /**
         * 用于骗过lambda的校验
         */
        final double lambdaRet = result;
        /**
         * 生成详情
         */
        DetailCallable detailCallable = () -> {
            FourCalculationDetail detail = new FourCalculationDetail();
            detail.setLeft(leftVariable);
            detail.setRight(rightVariable);
            detail.setResult(lambdaRet);
            detail.setDescription(description);
            detail.setConditionUuid(this.getConditionUuid());
            detail.setRuleUuid(this.getRuleUuid());
            return detail;
        };
        return new FunctionResult(lambdaRet, detailCallable);
    }
}
