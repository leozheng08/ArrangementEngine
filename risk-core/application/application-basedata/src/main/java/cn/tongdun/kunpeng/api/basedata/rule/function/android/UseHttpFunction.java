package cn.tongdun.kunpeng.api.basedata.rule.function.android;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.common.Constant;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class UseHttpFunction extends AbstractFunction {

    @Override
    public String getName() {
        return Constant.Function.ANDROID_HTTP;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {

    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

        boolean result = false;
        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            result = false;
        }
        else {
            Object isUseHttpProxy = deviceInfo.get("proxyStr");
            Object isUseHttpProxyNew = deviceInfo.get("proxyInfo");
            if (isUseHttpProxy != null && StringUtils.isNotBlank(isUseHttpProxy.toString())) {
                result = true;
            }
            else if (isUseHttpProxyNew != null && StringUtils.isNotBlank(isUseHttpProxyNew.toString())) {
                result = true;
            }
            else {
                result = false;
            }
        }

        return new FunctionResult(result);
    }


}
