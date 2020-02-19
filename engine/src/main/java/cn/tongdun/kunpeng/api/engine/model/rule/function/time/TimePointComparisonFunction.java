package cn.tongdun.kunpeng.api.engine.model.rule.function.time;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.engine.model.rule.util.DateUtil;
import cn.tongdun.kunpeng.api.engine.model.rule.util.TimeSlice;
import cn.tongdun.kunpeng.api.ruledetail.TimePointComparisonDetail;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;

public class TimePointComparisonFunction extends AbstractFunction {

    private String calcField;
    private String timeunit;
    private String lowerLimit;
    private String upperLimit;

    @Override
    public String getName() {
        return Constant.Function.TIME_TIME_POINT_COMPARISON;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("TimePointComparison function parse error,no params!");
        }

        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("calcField", param.getName())) {
                calcField = param.getValue();
            }
            else if (StringUtils.equals("timeunit", param.getName())) {
                timeunit = param.getValue();
            }
            else if (StringUtils.equals("lowerLimit", param.getName())) {
                lowerLimit = param.getValue();
            }
            else if (StringUtils.equals("upperLimit", param.getName())) {
                upperLimit = param.getValue();
            }
        });

        if (StringUtils.isAnyBlank(lowerLimit, upperLimit)) {
            throw new ParseException("TimePointComparison function lowerLimit or upperLimit error");
        }
    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Date date = DateUtil.getDateValue(context.get(calcField));
        if (null == date) {
            return new FunctionResult(false);
        }

        int dateValue = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (TimeSlice.YEAR.equals(timeunit)) {
            dateValue = calendar.get(Calendar.YEAR);
        }
        else if (TimeSlice.MONTH.equals(timeunit)) {
            dateValue = calendar.get(Calendar.MONTH);
        }
        else if (TimeSlice.WEEK.equals(timeunit)) {
            dateValue = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        }
        else if (TimeSlice.DAY.equals(timeunit)) {
            dateValue = calendar.get(Calendar.DAY_OF_MONTH);
        }
        else if (TimeSlice.HOUR.equals(timeunit)) {
            dateValue = calendar.get(Calendar.HOUR_OF_DAY);
        }
        else if (TimeSlice.MINUTE.equals(timeunit)) {
            dateValue = calendar.get(Calendar.MINUTE);
        }
        else if (TimeSlice.SECOND.equals(timeunit)) {
            dateValue = calendar.get(Calendar.SECOND);
        }

        boolean result = false;
        if (StringUtils.isNotBlank(upperLimit) && StringUtils.isNotBlank(lowerLimit)) {
            result = isInTimeRange(dateValue, lowerLimit, upperLimit);
        }


        DetailCallable detailCallable = null;
        if (result) {
            int finalDateValue = dateValue;
            detailCallable = () -> {
                TimePointComparisonDetail detail = null;
                detail = new TimePointComparisonDetail();
                detail.setConditionUuid(conditionUuid);
                detail.setRuleUuid(ruleUuid);
                detail.setDate(date);
                detail.setTimeSlice(timeunit);
                detail.setResult((double) finalDateValue);
                return detail;
            };
        }

        return new FunctionResult(result, detailCallable);
    }


    private boolean isInTimeRange(int dateValue, String lowerLimit, String upperLimit) {
        boolean result = false;
        int lowerLimitVal = Integer.parseInt(lowerLimit);
        int upperLimitVal = Integer.parseInt(upperLimit);
        if ((dateValue >= lowerLimitVal) && (dateValue <= upperLimitVal)) {
            result = true;
        }
        return result;
    }
}
