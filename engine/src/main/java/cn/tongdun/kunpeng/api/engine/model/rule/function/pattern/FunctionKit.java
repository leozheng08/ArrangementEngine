package cn.tongdun.kunpeng.api.engine.model.rule.function.pattern;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.Variable;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.tongdun.kunpeng.api.engine.model.rule.function.VelocityFuncType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionKit extends AbstractFunction {
    private static final Logger logger = LoggerFactory.getLogger(FunctionKit.class);

//[
//    {
//        "name": "funcType",
//            "type": "enum",
//            "value": "VelocityFuncType.SUM"
//    },
//    {
//        "name": "calVariable",
//            "type": "GAEA_INDICATRIX",
//            "value": "391277626028733417"
//    },
//    {
//        "name": "otherCalVariables",
//            "type": "GAEA_INDICATRIX",
//            "value": "391248999354725353",
//            "index": 0
//    },
//    {
//        "name": "naturalValue",
//            "type": "double",
//            "value": "0"
//    }
//]


//[
//    {
//        "name": "calVariable",
//            "type": "index",
//            "value": "0679f4d11a4d4931921c1a3b0e6e2b83"
//    },
//    {
//        "name": "otherCalVariables",
//            "type": "index",
//            "value": "cc406b8f99a646a8b5a5e6c5a57fd90e"
//    },
//    {
//        "name": "funcType",
//            "type": "enum",
//            "value": "VelocityFuncType.SUM"
//    }
//]


//[
//    {
//        "name": "funcType",
//            "type": "enum",
//            "value": "VelocityFuncType.INDEX"
//    },
//    {
//        "name": "calVariable",
//            "type": "GAEA_INDICATRIX",
//            "value": "252524598116209673"
//    },
//    {
//        "name": "otherCalVariables",
//            "type": "index",
//            "value": "",
//            "index": 0
//    },
//    {
//        "name": "naturalValue",
//            "type": "double",
//            "value": "10"
//    }
//]


    private VelocityFuncType funcType;
    private Variable calVariableVar;
    private List<Variable> otherCalVariablesVar;
    private Integer naturalValue;


    @Override
    public String getName() {
        return "pattern/functionKit";
    }

    @Override
    public void parse(List<FunctionParam> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        List<FunctionParam> lstOther = list.stream()
                .filter(p -> "otherCalVariables".equals(p.getName()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(lstOther)) {
            otherCalVariablesVar = buildParam(lstOther);
        }

        list.forEach(param -> {
            if (StringUtils.equals("funcType", param.getName())) {
                String funcTypeValue = String.valueOf(param.getValue());
                if (StringUtils.startsWith(funcTypeValue, "VelocityFuncType.")) {
                    funcType = VelocityFuncType.valueOf(funcTypeValue.split("VelocityFuncType.")[1]);
                }
                else {
                    funcType = VelocityFuncType.valueOf(funcTypeValue);
                }
            }
            else if (StringUtils.equals("calVariable", param.getName())) {
                calVariableVar = buildParam(param);
            }
            else if (StringUtils.equals("naturalValue", param.getName())) {
                naturalValue = Integer.parseInt(param.getValue());
            }

        });


    }

    @Override
    public CalculateResult run(ExecuteContext context) {
//        @Param("funcType") VelocityFuncType funcType,
//        @Param("calVariable") Double variable,
//        @Param("otherCalVariables") List<Double> otherVariables,
//        @Param("naturalValue") Integer naturalValue,


        Double calVariable = (Double) calVariableVar.eval(context);
        List<Double> otherCalVariables = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(otherCalVariablesVar)) {
            for (Variable variable : otherCalVariablesVar) {
                otherCalVariables.add((Double) variable.eval(context));
            }
        }


        double result = Double.NaN;

        if (null == otherCalVariables || null == funcType) {
            logger.warn("functionKit get null parameter, otherVariables or funcType");
            return new CalculateResult(false, null);
        }

        List<Double> values = new ArrayList<>();
        values.add(calVariable);
        values.addAll(otherCalVariables);


        result = statisticCalculate(values, funcType, naturalValue);


        return new CalculateResult(true, null);
    }


    public static double statisticCalculate(List<Double> values, VelocityFuncType funcType, int naturalValue) {
        double result = Double.NaN;
        if (null == values || 0 == values.size() || null == funcType) {
            return result;
        }

        DescriptiveStatistics statistics = new DescriptiveStatistics();
        for (Double value : values) {
            if (null != value && !Double.isNaN(value)) {
                statistics.addValue(value);
            }
        }

        result = getCalculateResult(funcType, statistics, naturalValue);
        return result;
    }

    public static double getCalculateResult(VelocityFuncType funcType, DescriptiveStatistics statistics, int naturalValue) {
        try {
            switch (funcType) {
                case SUM:
                    return statistics.getSum();
                case AVERAGE:
                    return statistics.getMean();
                case STANDARDDEVIATION:
                case STDEV:
                    return statistics.getStandardDeviation();
                case VARIANCE:
                    return statistics.getVariance();
                case MAX:
                    return statistics.getMax();
                case MIN:
                    return statistics.getMin();
                case MEDIAN:
                    return statistics.getPercentile(50);
                case POWER:
                    statistics.addValue(naturalValue);
                    return getPower(statistics);
                case INDEX:
                    statistics.addValue(naturalValue);
                    return getIndex(statistics);
                case NATURAL_INDEX:
                    return getNaturalIndex(statistics);
                case LOGARITHM:
                    statistics.addValue(naturalValue);
                    return getLogarithm(statistics);
                case NATURAL_LOGARITHM:
                    return getNaturalLogarithm(statistics);
                default:
                    return Double.NaN;
            }
        }
        catch (Exception e) {
            logger.warn("calculate error", e);
            return Double.NaN;
        }
    }

    /**
     * 求幂a^b
     */
    public static double getPower(DescriptiveStatistics ds) {
        double[] values = ds.getValues();
        if (values.length < 3) {
            return Double.NaN;
        }
        return Math.pow(values[0], values[values.length - 1]);
    }

    /**
     * 求指数b^a
     */
    private static double getIndex(DescriptiveStatistics statistics) {
        double[] values = statistics.getValues();
        if (values.length < 3) {
            return Double.NaN;
        }
        double index = values[0];
        double base = values[values.length - 1];
        return Math.pow(base, index);
    }

    /**
     * 求自然指数e^a
     */
    private static double getNaturalIndex(DescriptiveStatistics statistics) {
        return Math.pow(Math.E, statistics.getElement(0));
    }

    /**
     * 求对数log(b^a)
     */
    public static double getLogarithm(DescriptiveStatistics ds) {
        double[] values = ds.getValues();
        if (values.length < 3) {
            return Double.NaN;
        }
        double value = values[0];
        double base = values[values.length - 1];
        if (base <= 1) {
            return Double.NaN;
        }
        if (value <= 0) {
            return Double.NaN;
        }
        return Math.log(value) / Math.log(base);
    }

    /**
     * 求自然对数log(e^a)
     */
    private static double getNaturalLogarithm(DescriptiveStatistics statistics) {
        double value = statistics.getElement(0);
        if (value <= 0) {
            return Double.NaN;
        }
        return Math.log(value) / Math.log(Math.E);
    }


}
