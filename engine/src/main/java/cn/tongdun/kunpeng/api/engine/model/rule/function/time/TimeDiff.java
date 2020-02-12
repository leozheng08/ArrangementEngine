package cn.tongdun.kunpeng.api.engine.model.rule.function.time;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.tongdun.kunpeng.api.engine.model.rule.util.DateUtil;
import cn.tongdun.kunpeng.common.Constant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

public class TimeDiff extends AbstractFunction {
//[
//  {
//    "name": "timeA",
//    "type": "string",
//    "value": "eventOccurTime"
//  },
//  {
//    "name": "timeB",
//    "type": "string",
//    "value": "time2"
//  },
//  {
//    "name": "timeOperator",
//    "type": "string",
//    "value": ">"
//  },
//  {
//    "name": "timeslice",
//    "type": "string",
//    "value": "1"
//  },
//  {
//    "name": "timeunit",
//    "type": "string",
//    "value": "s"
//  }
//]

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
    public void parse(List<FunctionParam> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.forEach(functionParam -> {
            if (StringUtils.equals("timeA", functionParam.getName())) {
                timeA = functionParam.getValue();
            }
            else if (StringUtils.equals("timeB", functionParam.getName())) {
                timeB = functionParam.getValue();
            }
            else if (StringUtils.equals("timeOperator", functionParam.getName())) {
                timeOperator = functionParam.getValue();
            }
            else if (StringUtils.equals("timeslice", functionParam.getName())) {
                timeslice = functionParam.getValue();
            }
            else if (StringUtils.equals("timeunit", functionParam.getName())) {
                timeunit = functionParam.getValue();
            }
        });
    }

    @Override
    public CalculateResult run(ExecuteContext context) {
        Boolean result = Boolean.FALSE;

        Date date1 = DateUtil.getDateValue(context.getField(timeA));
        Date date2 = DateUtil.getDateValue(context.getField(timeB));
        if (date1 == null || date2 == null) {
            return new CalculateResult(Boolean.FALSE, null);
        }

        if (null != timeOperator && null != timeunit) {
            int value = null == timeslice ? 0 : Integer.parseInt(timeslice);
            long diffMs = date1.getTime() - date2.getTime();
            long diff = DateUtil.getValueByTimeSlice(timeunit, diffMs, "ceil");
            result = timeDiffResult(timeOperator, value, diff);
        }

        return new CalculateResult(result, null);
    }

    private Boolean timeDiffResult(String operator, int value, long diff) {
        Boolean result = Boolean.FALSE;

        if ("none".equalsIgnoreCase(operator)) {
            // FIXME: 2020-01-10 handle none
            result = false;
        }
        else if (">".equals(operator)) {
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
}
