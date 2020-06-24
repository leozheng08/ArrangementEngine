package cn.tongdun.kunpeng.api.basedata.rule.function.anomaly;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.ruledetail.DeviceSampleDetail;

import java.util.Map;

public class WebDeviceSampleExcetionFunction extends AbstractFunction {


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
            DetailCallable detailCallable = ()->{
                DeviceSampleDetail deviceSampleDetail = new DeviceSampleDetail();
                deviceSampleDetail.setConditionUuid(this.conditionUuid);
                deviceSampleDetail.setRuleUuid(this.ruleUuid);
                deviceSampleDetail.setDescription(this.description);
                deviceSampleDetail.setDeviceId(deviceId);
                deviceSampleDetail.setImageLoaded(false);
                return deviceSampleDetail;
            };
            return new FunctionResult(true, detailCallable);
        } else {
            return new FunctionResult(false);
        }
    }


}
