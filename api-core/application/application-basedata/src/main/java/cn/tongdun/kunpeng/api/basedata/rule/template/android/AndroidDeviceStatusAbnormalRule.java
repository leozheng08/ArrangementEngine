package cn.tongdun.kunpeng.api.basedata.rule.template.android;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.EvalResult;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.rule.AbstractRule;
import cn.fraudmetrix.module.tdrule.util.FunctionLoader;
import cn.tongdun.kunpeng.api.basedata.rule.function.android.AndroidDeviceStatusAbnormalFunction;

/**
 * @Author: liuq
 * @Date: 2020/5/27 3:10 下午
 */
public class AndroidDeviceStatusAbnormalRule extends AbstractRule {

    private AndroidDeviceStatusAbnormalFunction function;

    @Override
    public EvalResult run(ExecuteContext executeContext) {
        Object ret = function.eval(executeContext);
        return EvalResult.valueOf(ret);
    }

    @Override
    public void parse(RawRule rawRule) {
        if (null == rawRule || rawRule.getFunctionDescList() == null || rawRule.getFunctionDescList().isEmpty()) {
            throw new ParseException("AndroidDeviceStatusAbnormalRule parse error!null == rawRule or rawRule.getFunctionDescList is blank!");
        }
        if (rawRule.getFunctionDescList().size() > 1) {
            throw new ParseException("AndroidDeviceStatusAbnormalRule parse error!expect 1 FunctionDesc,but input :" + rawRule.getFunctionDescList().size());
        }

        function = (AndroidDeviceStatusAbnormalFunction) FunctionLoader.getFunction(rawRule.getFunctionDescList().get(0));
    }
}
