package cn.tongdun.kunpeng.api.engine.model.rule.function;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.Literal;
import cn.fraudmetrix.module.tdrule.eval.Variable;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.tongdun.kunpeng.api.engine.model.rule.function.pattern.AbstractCalculateFunction;
import cn.tongdun.kunpeng.api.engine.model.rule.util.DataUtil;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * @Author: liuq
 * @Date: 2020/2/12 5:27 PM
 */
public class WeightFunction extends AbstractCalculateFunction {

    private static Logger logger = LoggerFactory.getLogger(WeightFunction.class);

    private double baseWeight = 0;
    private double weightRatio = 0;
    private Double lowerLimitScore = -30D;
    private Double upperLimitScore = 30D;

    private Variable weightProperty;

    @Override
    public String getName() {
        return "ruleWeight";
    }

    @Override
    protected FunctionResult run(ExecuteContext executeContext) {
        Double weight = null;
        try {
            weight = DataUtil.toDouble(weightProperty.eval(executeContext));
        } catch (Exception e){
            logger.warn(TraceUtils.getFormatTrace()+"WeightFunction weightIndex eval error",e);
        }
        if (null == weight) {
            weight = 0D;
        }

        weight = weight * weightRatio;
        if (weight > upperLimitScore) {
            weight = upperLimitScore;
        }
        if (weight < lowerLimitScore) {
            weight = lowerLimitScore;
        }
        Double total = baseWeight + weight;
        BigDecimal bd = new BigDecimal(total).setScale(0, BigDecimal.ROUND_HALF_UP);
        return new FunctionResult(Integer.valueOf(bd.toString()));
        //return new FunctionResult(total.intValue());
    }

    @Override
    protected void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("FourCalculation function parse error,no params!");
        }
        functionDesc.getParamList().stream().forEach(functionParam -> {
            switch (functionParam.getName()) {
                case "baseWeight":
                    baseWeight = DataUtil.toDouble(functionParam.getValue());
                    break;
                case "weightRatio":
                    weightRatio = DataUtil.toDouble(functionParam.getValue());
                    break;
                case "lowerLimitScore":
                    lowerLimitScore = DataUtil.toDouble(functionParam.getValue());
                    break;
                case "upperLimitScore":
                    upperLimitScore = DataUtil.toDouble(functionParam.getValue());
                    break;
                case "weightProperty":
                    if(StringUtils.isBlank(functionParam.getType())||StringUtils.isBlank(functionParam.getValue())){
                        weightProperty = new Literal(0D);
                    } else {
                        weightProperty = buildVariable(functionParam, "Double");
                    }
                    break;
                default:
            }
        });
    }
}
