package cn.tongdun.kunpeng.api.engine.model.rule.function.pattern;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.Variable;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FourCalculation extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(FourCalculation.class);

//[
//    {
//        "name": "leftVariable",
//            "type": "GAEA_INDICATRIX",
//            "value": "391285645126939625"
//    },
//    {
//        "name": "arithmetic",
//            "type": "string",
//            "value": "plus"
//    },
//    {
//        "name": "rightVariable",
//            "type": "GAEA_INDICATRIX",
//            "value": "391277626028733417"
//    }
//]

//[
//    {
//        "name": "leftVariable",
//            "type": "input",
//            "value": "1"
//    },
//    {
//        "name": "arithmetic",
//            "type": "string",
//            "value": "plus"
//    },
//    {
//        "name": "rightVariable",
//            "type": "input",
//            "value": "1"
//    }
//]


//[
//  {
//    "name": "leftVariable",
//    "type": "index",
//    "value": "9b0f7c774c884b78a38a668cdf930dee"
//  },
//  {
//    "name": "arithmetic",
//    "type": "string",
//    "value": "plus"
//  },
//  {
//    "name": "rightVariable",
//    "type": "input",
//    "value": "123"
//  }
//]


    private Variable leftVariableVar;
    private Variable arithmeticVar;
    private Variable rightVariableVar;


    @Override
    public String getName() {
        return "pattern/fourCalculation";
    }

    @Override
    public void parse(List<FunctionParam> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.forEach(param -> {
            if (StringUtils.equals("leftVariable", param.getName())) {
                leftVariableVar = buildParam(param, Double.class);
            }
            else if (StringUtils.equals("arithmetic", param.getName())) {
                arithmeticVar = buildParam(param);
            }
            else if (StringUtils.equals("rightVariable", param.getName())) {
                rightVariableVar = buildParam(param, Double.class);
            }

        });
    }

    @Override
    public CalculateResult run(ExecuteContext context) {
//        @Param("leftVariable") Object leftVariable,
//        @Param("arithmetic") String arithmetic,
//        @Param("rightVariable") Object rightVariable,


        Double leftVariable = (Double) leftVariableVar.eval(context);
        String arithmetic = (String) arithmeticVar.eval(context);
        Double rightVariable = (Double) rightVariableVar.eval(context);

        if (StringUtils.isBlank(arithmetic)) {
            logger.warn("fourCalculation get  null arithmetic");
            return new CalculateResult(false, null);
        }
        if (null == leftVariable || null == rightVariable) {
            logger.warn("fourCalculation get null leftVariable or rightVariable");
            return new CalculateResult(false, null);
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
                }
                else {
                    logger.warn("calculate fourCalculation occur exception : divisor can not be zero");
                }
                break;
            default:
        }

        return new CalculateResult(true, null);
    }
}
