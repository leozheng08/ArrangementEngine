package cn.tongdun.kunpeng.api.basedata.rule.function.anomaly;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.common.Constant;

import java.util.Map;

public class Profile extends AbstractFunction {


    @Override
    public String getName() {
        return Constant.Function.ANOMALY_PROFILE;
    }


    @Override
    public void parse(FunctionDesc functionDesc) {

    }

    @Override
    public Object eval(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

        Map<String, Object> map = context.getDeviceInfo();
        boolean imageLoaded = false;
        String deviceId = (String) map.get("deviceId");

        if (map.get("imageLoaded") != null) {
            imageLoaded = Boolean.valueOf((String) map.get("imageLoaded"));
        }

        if (!imageLoaded && deviceId != null) {

            return true;
        }
        else {
            return false;
        }
    }


}
