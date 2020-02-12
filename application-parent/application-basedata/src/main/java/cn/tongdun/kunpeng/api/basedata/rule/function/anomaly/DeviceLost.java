package cn.tongdun.kunpeng.api.basedata.rule.function.anomaly;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.common.Constant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class DeviceLost extends AbstractFunction {


    private String codes;


    @Override
    public String getName() {
        return Constant.Function.ANOMALY_DEVICE;
    }


    @Override
    public void parse(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("anomaly DeviceLost function parse error,no params!");
        }

        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("codes", param.getName())) {
                codes = param.getValue();
            }
        });
    }

    @Override
    public Object eval(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;
        try {
            Map<String, Object> deviceInfo = context.getDeviceInfo();
            if (deviceInfo == null) {
                return true;
            }

            String deviceId = (String) context.get("deviceId");
            String code = (String) deviceInfo.get("code");
            if (code == null) {
                if (StringUtils.isNotBlank(deviceId)) {
                    return false;
                }
                else {
                    return true;
                }
            }

            if (StringUtils.isNotBlank(codes) && codes.contains(code)) {
                return true;
            }

            return false;
        }
        catch (Exception e) {
            return false;
        }
    }


}
