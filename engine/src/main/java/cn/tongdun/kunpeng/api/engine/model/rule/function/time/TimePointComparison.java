package cn.tongdun.kunpeng.api.engine.model.rule.function.time;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.tongdun.kunpeng.api.engine.model.rule.util.DateUtil;
import cn.tongdun.kunpeng.api.engine.model.rule.util.TimeSlice;
import cn.tongdun.kunpeng.common.Constant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimePointComparison extends AbstractFunction {
//[
//  {
//    "name": "calcField",
//    "type": "string",
//    "value": "eventOccurTime"
//  },
//  {
//    "name": "timeunit",
//    "type": "string",
//    "value": "h"
//  },
//  {
//    "name": "lowerLimit",
//    "type": "string",
//    "value": "1"
//  },
//  {
//    "name": "upperLimit",
//    "type": "string",
//    "value": "4"
//  }
//]

    private String calcField;
    private String timeunit;
    private String lowerLimit;
    private String upperLimit;


    @Override
    public String getName() {
        return Constant.Function.TIME_TIME_POINT_COMPARISON;
    }

    @Override
    public void parse(List<FunctionParam> list) {
        if(CollectionUtils.isEmpty(list)){
            return;
        }

        list.forEach(functionParam -> {
            if (StringUtils.equals("calcField", functionParam.getName())) {
                calcField = functionParam.getValue();
            }
            else if (StringUtils.equals("timeunit", functionParam.getName())) {
                timeunit = functionParam.getValue();
            }
            else if (StringUtils.equals("lowerLimit", functionParam.getName())) {
                lowerLimit = functionParam.getValue();
            }
            else if (StringUtils.equals("upperLimit", functionParam.getName())) {
                upperLimit = functionParam.getValue();
            }
        });
    }

    @Override
    public CalculateResult run(ExecuteContext context) {
        Date date = DateUtil.getDateValue(context.getField(calcField));
        if (null == date) {
            return new CalculateResult(false, null);
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

        return new CalculateResult(result, null);
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
