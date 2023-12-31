package cn.tongdun.kunpeng.api.engine.model.rule.function.pattern;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.Variable;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.engine.model.rule.function.VelocityFuncType;
import cn.tongdun.kunpeng.api.ruledetail.FunctionKitDetail;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionKit extends AbstractCalculateFunction {

    private static final Logger logger = LoggerFactory.getLogger(FunctionKit.class);

    private VelocityFuncType funcType;
    private Variable calVariableVar;
    private List<Variable> otherCalVariablesVar;
    private Integer naturalValue;


    @Override
    public String getName() {
        return "pattern/functionKit";
    }

    @Override
    public void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("FunctionKit function parse error,no params!");
        }

        List<FunctionParam> lstOther = functionDesc.getParamList().stream()
                .filter(p -> "otherCalVariables".equals(p.getName()))
                .collect(Collectors.toList());
        otherCalVariablesVar = Lists.newArrayList();
        for (FunctionParam functionParam : lstOther) {
            otherCalVariablesVar.add(buildVariable(functionParam, "double"));
        }

        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("funcType", param.getName())) {
                String funcTypeValue = param.getValue();
                if (StringUtils.startsWith(funcTypeValue, "VelocityFuncType.")) {
                    funcType = VelocityFuncType.valueOf(funcTypeValue.split("VelocityFuncType.")[1]);
                } else {
                    funcType = VelocityFuncType.valueOf(funcTypeValue);
                }
            } else if (StringUtils.equals("calVariable", param.getName())) {
                calVariableVar = buildVariable(param, "double");
            } else if (StringUtils.equals("naturalValue", param.getName())) {
                naturalValue = Integer.parseInt(param.getValue());
            }
        });
        if (null == funcType || null == calVariableVar) {
            throw new ParseException("FunctionKit function parse error,null == funcType || null == calVariableVar,conditionUuid:"
                    + functionDesc.getConditionUuid() + ",ruleUuid:" + functionDesc.getRuleUuid());
        }
    }

    @Override
    public FunctionResult run(ExecuteContext context) {
        double result;
        List<Double> values = new ArrayList<>();
        try {
            values.add((Double) calVariableVar.eval(context));
            if (CollectionUtils.isNotEmpty(otherCalVariablesVar)) {
                for (Variable variable : otherCalVariablesVar) {
                    values.add((Double) variable.eval(context));
                }
            }
        } catch (Exception e) {
            logger.warn(TraceUtils.getFormatTrace() + "FunctionKit eval error!", e);
        }

        //修复NPE问题，函数工具箱中，正常情况下这个值其实一直都是0
        naturalValue = naturalValue == null ? 0 : naturalValue;
        result = statisticCalculate(values, funcType, naturalValue);

        /**
         * 生成详情
         */
        final List<Double> valuesDetail = values;
        DetailCallable detailCallable = () -> {
            FunctionKitDetail detail = new FunctionKitDetail();
            detail.setResult(result);
            List<Number> vs = new ArrayList<>(values.size());
            vs.addAll(valuesDetail);
            detail.setVariables(vs);
            detail.setDescription(description);
            detail.setConditionUuid(this.getConditionUuid());
            detail.setRuleUuid(this.getRuleUuid());
            return detail;
        };
        return new FunctionResult(result, detailCallable);
    }


    private static double statisticCalculate(List<Double> values, VelocityFuncType funcType, int naturalValue) {
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

    private static double getCalculateResult(VelocityFuncType funcType, DescriptiveStatistics statistics, int naturalValue) {
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
        } catch (Exception e) {
            logger.warn(TraceUtils.getFormatTrace() + "calculate error", e);
            return Double.NaN;
        }
    }

    /**
     * 求幂a^b
     */
    private static double getPower(DescriptiveStatistics ds) {
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
    private static double getLogarithm(DescriptiveStatistics ds) {
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
