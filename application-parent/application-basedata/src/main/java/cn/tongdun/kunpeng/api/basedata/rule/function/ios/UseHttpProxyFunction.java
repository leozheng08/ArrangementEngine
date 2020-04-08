package cn.tongdun.kunpeng.api.basedata.rule.function.ios;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;

import java.util.Map;

public class UseHttpProxyFunction extends AbstractFunction {

    @Override
    public String getName() {
        return Constant.Function.IOS_USE_HTTP_PROXY;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {

    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            return new FunctionResult(false);
        }
        else {
            Object proxyType = deviceInfo.get("proxyType");
            if (proxyType != null && !"none".equalsIgnoreCase(proxyType.toString())) {
                return new FunctionResult(true);
            }
            else {
                return new FunctionResult(false);
            }
        }
    }


}
