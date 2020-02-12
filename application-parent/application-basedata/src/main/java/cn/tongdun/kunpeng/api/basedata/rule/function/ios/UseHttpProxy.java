package cn.tongdun.kunpeng.api.basedata.rule.function.ios;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;

import java.util.Map;

public class UseHttpProxy extends AbstractFunction {

    @Override
    public String getName() {
        return Constant.Function.IOS_USE_HTTP_PROXY;
    }


    @Override
    public void parse(FunctionDesc functionDesc) {

    }

    @Override
    public Object eval(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            return false;
        }
        else {
            Object proxyType = deviceInfo.get("proxyType");
            if (proxyType != null && !"none".equalsIgnoreCase(proxyType.toString())) {
                return true;
            }
            else {
                return false;
            }
        }
    }


}
