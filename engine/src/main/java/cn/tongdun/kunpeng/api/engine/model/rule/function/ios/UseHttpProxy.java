package cn.tongdun.kunpeng.api.engine.model.rule.function.ios;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;

import java.util.List;
import java.util.Map;

public class UseHttpProxy extends AbstractFunction {

    @Override
    public String getName() {
        return Constant.Function.IOS_USE_HTTP_PROXY;
    }

    @Override
    public void parse(List<FunctionParam> functionParamList) {

    }

    @Override
    public CalculateResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            return new CalculateResult(false, null);
        }
        else {
            Object proxyType = deviceInfo.get("proxyType");
            if (proxyType != null && !"none".equalsIgnoreCase(proxyType.toString())) {
                return new CalculateResult(true, null);
            }
            else {
                return new CalculateResult(false, null);
            }
        }
    }
}
