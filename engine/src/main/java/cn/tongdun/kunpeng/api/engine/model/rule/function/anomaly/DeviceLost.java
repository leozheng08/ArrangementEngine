package cn.tongdun.kunpeng.api.engine.model.rule.function.anomaly;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class DeviceLost extends AbstractFunction {
//[
//    {
//        "name": "codes",
//            "type": "string",
//            "value": ",065,412,074,411,061"
//    }
//]


    private String codes;


    @Override
    public String getName() {
        return Constant.Function.ANOMALY_DEVICE;
    }

    @Override
    public void parse(List<FunctionParam> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.forEach(functionParam -> {
            if (StringUtils.equals("codes", functionParam.getName())) {
                codes = functionParam.getValue();
            }
        });
    }

    @Override
    public CalculateResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;
        try {
            Map<String, Object> deviceInfo = context.getDeviceInfo();
            if (deviceInfo == null) {
                return new CalculateResult(true, null);
            }

            String deviceId = (String) context.get("deviceId");
            String code = (String) deviceInfo.get("code");
            if (code == null) {
                if (StringUtils.isNotBlank(deviceId)) {
                    return new CalculateResult(false, null);
                }
                else {
                    return new CalculateResult(true, null);
                }
            }

            if (StringUtils.isNotBlank(codes) && codes.contains(code)) {
                return new CalculateResult(true, null);
            }

            return new CalculateResult(false, null);
        }
        catch (Exception e) {
            return new CalculateResult(false, null);
        }
    }
}
