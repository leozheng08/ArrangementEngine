package cn.tongdun.kunpeng.api.engine.model.rule.function.time;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.engine.model.rule.util.DateUtil;
import cn.tongdun.kunpeng.api.ruledetail.TimeDiffDetail;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class TimeDiffFunction extends AbstractFunction {

    private String timeA;
    private String timeB;
    private String timeOperator;
    private String timeslice;
    private String timeunit;


    @Override
    public String getName() {
        return Constant.Function.TIME_TIME_DIFFER;
    }

    @Override
    public void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("TimeDiff function parse error, no params!");
        }

        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("timeA", param.getName())) {
                timeA = param.getValue();
            }
            else if (StringUtils.equals("timeB", param.getName())) {
                timeB = param.getValue();
            }
            else if (StringUtils.equals("timeOperator", param.getName())) {
                timeOperator = param.getValue();
            }
            else if (StringUtils.equals("timeslice", param.getName())) {
                timeslice = param.getValue();
            }
            else if (StringUtils.equals("timeunit", param.getName())) {
                timeunit = param.getValue();
            }
        });

        if (StringUtils.isBlank(timeOperator) || "none".equals(timeOperator)) {
            throw new ParseException("TimeDiff function timeOperator illegal");
        }
    }


    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Date dateA = DateUtil.getDateValue(context.get(timeA));
        Date dateB = DateUtil.getDateValue(context.get(timeB));
        if (null == dateA || null == dateB) {
            return new FunctionResult(false);
        }

        boolean result = false;
        long diffMs = 0;
        long diff = 0;
        if (null != timeOperator && null != timeunit) {
            int value = null == timeslice ? 0 : Integer.parseInt(timeslice);
            diffMs = dateA.getTime() - dateB.getTime();
            diff = DateUtil.getValueByTimeSlice(timeunit, diffMs, "ceil");
            result = timeDiffResult(timeOperator, value, diff);
        }

        DetailCallable detailCallable = null;
        if (result) {
            long finalDiffMs = diffMs;
            long finalDiff = diff;
            detailCallable = () -> {
                TimeDiffDetail detail = new TimeDiffDetail();
                detail.setConditionUuid(conditionUuid);
                detail.setRuleUuid(ruleUuid);
                detail.setDateA(dateA);
                detail.setDateB(dateB);
                detail.setResult((double) finalDiffMs);
                String diffDisplay = finalDiff + getTimeSliceUnitDisplayName(timeunit);
                detail.setDiffDisplay(diffDisplay);
                return detail;
            };
        }

        return new FunctionResult(result, detailCallable);
    }


    private boolean timeDiffResult(String operator, int value, long diff) {
        boolean result = false;

        if (">".equals(operator)) {
            result = diff > value;
        }
        else if (">=".equals(operator)) {
            result = diff >= value;
        }
        else if ("<".equals(operator)) {
            result = diff < value;
        }
        else if ("<=".equals(operator)) {
            result = diff <= value;
        }
        else if ("==".equals(operator)) {
            result = diff == value;
        }

        return result;
    }


    private String getTimeSliceUnitDisplayName(String timeSliceUnit) {
        if (null == timeSliceUnit) {
            return null;
        }
        switch (timeSliceUnit) {
            case "y":
                return "年";
            case "M":
                return "月";
            case "d":
                return "天";
            case "h":
                return "小时";
            case "m":
                return "分钟";
            case "s":
                return "秒";
            default:
                return timeSliceUnit;
        }
    }

}
