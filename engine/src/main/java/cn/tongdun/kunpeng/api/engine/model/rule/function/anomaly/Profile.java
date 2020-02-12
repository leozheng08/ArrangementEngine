package cn.tongdun.kunpeng.api.engine.model.rule.function.anomaly;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;

import java.util.List;
import java.util.Map;

public class Profile extends AbstractFunction {


    @Override
    public String getName() {
        return Constant.Function.ANOMALY_PROFILE;
    }

    @Override
    public void parse(List<FunctionParam> functionParamList) {

    }

    @Override
    public CalculateResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Map<String, Object> map = context.getDeviceInfo();
        boolean imageLoaded = false;
        String deviceId = (String) map.get("deviceId");

        if (map.get("imageLoaded") != null) {
            imageLoaded = Boolean.valueOf((String) map.get("imageLoaded"));
        }

        if (!imageLoaded && deviceId != null) {

            return new CalculateResult(true, null);
        }
        else {
            return new CalculateResult(false, null);
        }
    }
}
