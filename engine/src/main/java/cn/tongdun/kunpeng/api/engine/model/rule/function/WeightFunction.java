package cn.tongdun.kunpeng.api.engine.model.rule.function;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.Variable;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.tongdun.kunpeng.api.engine.model.rule.function.pattern.AbstractCalculateFunction;
import org.apache.commons.collections.CollectionUtils;

/**
 * @Author: liuq
 * @Date: 2020/2/12 5:27 PM
 */
public class WeightFunction extends AbstractCalculateFunction {

    private double baseWeight = 0;
    private double weightRatio = 0;
    private Double downLimitScore = -30D;
    private Double upLimitScore = 30D;

    private Variable weightIndex;

    @Override
    public String getName() {
        return "ruleWeight";
    }

    @Override
    protected FunctionResult run(ExecuteContext executeContext) {
        Double weight = (Double) weightIndex.eval(executeContext);
        if (null == weight) {
            weight = 0D;
        }
        Double total = baseWeight + weight * weightRatio;
        if (total > upLimitScore) {
            total = upLimitScore;
        }
        if (total < downLimitScore) {
            total = downLimitScore;
        }
        return new FunctionResult(total.intValue());
    }

    @Override
    protected void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("FourCalculation function parse error,no params!");
        }
        functionDesc.getParamList().stream().forEach(functionParam -> {
            switch (functionParam.getName()) {
                case "baseWeight":
                    baseWeight = Double.valueOf(functionParam.getValue());
                    break;
                case "weightRatio":
                    weightRatio = Double.valueOf(functionParam.getValue());
                    break;
                case "downLimitScore":
                    downLimitScore = Double.valueOf(functionParam.getValue());
                    break;
                case "upLimitScore":
                    upLimitScore = Double.valueOf(functionParam.getValue());
                    break;
                case "weightIndex":
                    weightIndex = buildVariable(functionParam, null);
                    break;
                default:
            }
        });
    }
}
