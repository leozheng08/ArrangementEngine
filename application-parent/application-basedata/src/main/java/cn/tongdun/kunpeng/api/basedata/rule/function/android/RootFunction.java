package cn.tongdun.kunpeng.api.basedata.rule.function.android;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.common.Constant;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class RootFunction extends AbstractFunction {

    @Override
    public String getName() {
        return Constant.Function.ANDROID_ROOT;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {

    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            return new FunctionResult(false);
        }

        Object isRoot = deviceInfo.get("isRoot");
        Object isRootNew = deviceInfo.get("root");
        if (isRoot != null && StringUtils.equalsIgnoreCase(isRoot.toString(), "true")) {
            return new FunctionResult(true);
        }

        if (isRootNew != null && Boolean.TRUE.equals(isRootNew)) {
            return new FunctionResult(true);
        }

        return new FunctionResult(false);
    }


}
