package cn.tongdun.kunpeng.api.engine.model.rule.function.time;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.engine.constant.RuleConstant;
import cn.tongdun.kunpeng.api.engine.model.rule.util.DateUtil;
import cn.tongdun.kunpeng.api.engine.model.rule.util.TimeSlice;
import cn.tongdun.kunpeng.api.ruledetail.TimePointComparisonDetail;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;

public class TimePointFunction extends AbstractFunction {

    private String calcField;
    private String timeunit;
    private String lowerLimit;
    private String upperLimit;

    private boolean needJudge = false;


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
            } else if (StringUtils.equals("timeunit", param.getName())) {
                timeunit = param.getValue();
            } else if (StringUtils.equals("lowerLimit", param.getName())) {
                lowerLimit = param.getValue();
            } else if (StringUtils.equals("upperLimit", param.getName())) {
                upperLimit = param.getValue();
            }
        });
        if (functionDesc.getExtProperty(RuleConstant.FUNC_DESC_PARAMS_ALL) != null &&
                StringUtils.equalsIgnoreCase((CharSequence) functionDesc.getExtProperty(RuleConstant.FUNC_DESC_PARAMS_ALL), "true")) {
            needJudge = true;
        }
    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Date date = DateUtil.getDateValue(context.get(calcField));
        if (null == date) {
            if (needJudge) {
                return new FunctionResult(false);
            } else{
                return new FunctionResult(Double.NaN);
            }
        }

        int dateValue = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (TimeSlice.YEAR.equals(timeunit)) {
            dateValue = calendar.get(Calendar.YEAR);
        } else if (TimeSlice.MONTH.equals(timeunit)) {
            dateValue = calendar.get(Calendar.MONTH);
        } else if (TimeSlice.WEEK.equals(timeunit)) {
            dateValue = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        } else if (TimeSlice.DAY.equals(timeunit)) {
            dateValue = calendar.get(Calendar.DAY_OF_MONTH);
        } else if (TimeSlice.HOUR.equals(timeunit)) {
            dateValue = calendar.get(Calendar.HOUR_OF_DAY);
        } else if (TimeSlice.MINUTE.equals(timeunit)) {
            dateValue = calendar.get(Calendar.MINUTE);
        } else if (TimeSlice.SECOND.equals(timeunit)) {
            dateValue = calendar.get(Calendar.SECOND);
        }

        double finalDateValue = Integer.valueOf(dateValue).doubleValue();
        DetailCallable detailCallable = () -> {
            TimePointComparisonDetail detail = null;
            detail = new TimePointComparisonDetail();
            detail.setConditionUuid(conditionUuid);
            detail.setRuleUuid(ruleUuid);
            detail.setDescription(description);
            detail.setTime(date);
            detail.setTimeSlice(timeunit);
            detail.setResult(finalDateValue);
            return detail;
        };

        if (needJudge) {
            return new FunctionResult(isInTimeRange(dateValue, lowerLimit, upperLimit), detailCallable);
        }

        return new FunctionResult(finalDateValue, detailCallable);
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
