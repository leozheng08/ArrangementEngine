package cn.tongdun.kunpeng.api.engine.model.rule.template;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.EvalResult;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.rule.AbstractRule;
import cn.fraudmetrix.module.tdrule.util.FunctionLoader;
import cn.tongdun.kunpeng.api.engine.model.rule.function.time.TimePointFunction;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

public class TimePointComparisonRule extends AbstractRule {

    private Double lowerLimit;
    private Double upperLimit;
    private TimePointFunction function;


    @Override
    public EvalResult run(ExecuteContext executeContext) {
        Double functionRet = (Double) function.eval(executeContext);
        if (functionRet == null || functionRet.isNaN()) {
            return EvalResult.False;
        }
        return EvalResult.valueOf(functionRet >= lowerLimit && functionRet <= upperLimit);
    }

    @Override
    public void parse(RawRule rawRule) {
        if (null == rawRule || rawRule.getFunctionDescList() == null || rawRule.getFunctionDescList().isEmpty()) {
            throw new ParseException("GpsDistanceRule parse error!null == rawRule or rawRule.getFunctionDescList is blank!");
        }
        if (rawRule.getFunctionDescList().size() > 1) {
            throw new ParseException("GpsDistanceRule parse error!expect 1 FunctionDesc,but input :" + rawRule.getFunctionDescList().size());
        }

        FunctionDesc functionDesc = rawRule.getFunctionDescList().get(0);
        function = (TimePointFunction) FunctionLoader.getFunction(functionDesc);

        for (FunctionParam functionParam : functionDesc.getParamList()) {
            if (StringUtils.equalsIgnoreCase(functionParam.getName(), "lowerLimit")) {
                lowerLimit = Double.parseDouble(functionParam.getValue());
            } else if (StringUtils.equalsIgnoreCase(functionParam.getName(), "upperLimit")) {
                upperLimit = Double.parseDouble(functionParam.getValue());
            }
        }
        if (lowerLimit == null || lowerLimit.isNaN() || upperLimit == null || upperLimit.isNaN()) {
            throw new ParseException("TimePointComparisonRule parse error,lowerLimit or upperLimit error,functionDesc:" + JSON.toJSONString(functionDesc));
        }

    }
}
