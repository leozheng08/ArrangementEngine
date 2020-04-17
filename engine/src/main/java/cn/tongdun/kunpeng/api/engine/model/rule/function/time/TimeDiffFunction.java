package cn.tongdun.kunpeng.api.engine.model.rule.function.time;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.engine.model.rule.util.DateUtil;
import cn.tongdun.kunpeng.api.engine.model.rule.util.TimeSlice;
import cn.tongdun.kunpeng.api.ruledetail.TimeDiffDetail;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class TimeDiffFunction extends AbstractFunction {

    private String timeA;
    private String timeB;
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
            } else if (StringUtils.equals("timeB", param.getName())) {
                timeB = param.getValue();
            } else if (StringUtils.equals("timeslice", param.getName())) {
                timeslice = param.getValue();
            } else if (StringUtils.equals("timeunit", param.getName())) {
                timeunit = param.getValue();
            }
        });
    }


    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        double result=Double.NaN;
        Date dateA = DateUtil.getDateValue(context.get(timeA));
        Date dateB = DateUtil.getDateValue(context.get(timeB));
        if (null == dateA || null == dateB) {
            return new FunctionResult(result);
        }

        long diffMs = dateA.getTime() - dateB.getTime();
        result = DateUtil.getValueByTimeSlice(timeunit, diffMs, "ceil");

        final double ret = result;
        DetailCallable detailCallable = () -> {
            TimeDiffDetail detail = new TimeDiffDetail();
            detail.setConditionUuid(conditionUuid);
            detail.setRuleUuid(ruleUuid);
            detail.setDescription(description);

            detail.setDateA(dateA);
            detail.setDateB(dateB);
            detail.setResult(ret);
            String diffDisplay = ret + TimeSlice.getTimeSliceUnitDisplayName(timeunit);
            detail.setDiffDisplay(diffDisplay);
            return detail;
        };
        return new FunctionResult(result, detailCallable);
    }
}
