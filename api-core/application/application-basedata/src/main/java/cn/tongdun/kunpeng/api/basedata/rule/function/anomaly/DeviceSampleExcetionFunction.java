package cn.tongdun.kunpeng.api.basedata.rule.function.anomaly;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.common.Constant;

import java.util.Map;

public class DeviceSampleExcetionFunction extends AbstractFunction {


    @Override
    public String getName() {
        return Constant.Function.ANOMALY_PROFILE;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {

    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

        Map<String, Object> map = context.getDeviceInfo();
        boolean imageLoaded = false;
        String deviceId = (String) map.get("deviceId");

        if (map.get("imageLoaded") != null) {
            imageLoaded = Boolean.valueOf((String) map.get("imageLoaded"));
        }
        if (!imageLoaded && deviceId != null) {
            return new FunctionResult(true);
        } else {
            return new FunctionResult(false);
        }
    }


}
