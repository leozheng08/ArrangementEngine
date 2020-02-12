package cn.tongdun.kunpeng.api.engine.model.rule.function.android;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class UseHttp extends AbstractFunction {

    @Override
    public String getName() {
        return Constant.Function.ANDROID_HTTP;
    }

    @Override
    public void parse(List<FunctionParam> functionParamList) {

    }

    @Override
    public CalculateResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

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

        return new CalculateResult(result, null);
    }
}
