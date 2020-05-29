package cn.tongdun.kunpeng.api.basedata.rule.template.ios;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.EvalResult;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.rule.AbstractRule;
import cn.fraudmetrix.module.tdrule.util.FunctionLoader;
import cn.tongdun.kunpeng.api.basedata.rule.function.ios.IosDeviceStatusAbnormalFunction;

/**
 * @Author: liuq
 * @Date: 2020/5/27 3:14 下午
 */
public class IosDeviceStatusAbnormalRule extends AbstractRule {

    private IosDeviceStatusAbnormalFunction function;

    @Override
    public EvalResult run(ExecuteContext executeContext) {
        Object ret = function.eval(executeContext);
        return EvalResult.valueOf(ret);
    }

    @Override
    public void parse(RawRule rawRule) {
        if (null == rawRule || rawRule.getFunctionDescList() == null || rawRule.getFunctionDescList().isEmpty()) {
            throw new ParseException("IosDeviceStatusAbnormalRule parse error!null == rawRule or rawRule.getFunctionDescList is blank!");
        }
        if (rawRule.getFunctionDescList().size() > 1) {
            throw new ParseException("IosDeviceStatusAbnormalRule parse error!expect 1 FunctionDesc,but input :" + rawRule.getFunctionDescList().size());
        }

        function = (IosDeviceStatusAbnormalFunction) FunctionLoader.getFunction(rawRule.getFunctionDescList().get(0));
    }
}
