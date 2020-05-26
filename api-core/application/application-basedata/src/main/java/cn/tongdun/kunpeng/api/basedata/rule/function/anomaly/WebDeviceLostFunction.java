package cn.tongdun.kunpeng.api.basedata.rule.function.anomaly;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class WebDeviceLostFunction extends AbstractFunction {

    private static Logger logger = LoggerFactory.getLogger(WebDeviceLostFunction.class);

    private String codes;


    @Override
    public String getName() {
        return Constant.Function.ANOMALY_DEVICE;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {
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
    public FunctionResult run(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;
        try {
            Map<String, Object> deviceInfo = context.getDeviceInfo();
            if (deviceInfo == null) {
                return new FunctionResult(true);
            }

            String deviceId = (String) context.get("deviceId");
            String code = (String) deviceInfo.get("code");
            if (code == null) {
                if (StringUtils.isNotBlank(deviceId)) {
                    return new FunctionResult(false);
                } else {
                    return new FunctionResult(true);
                }
            }

            if (StringUtils.isNotBlank(codes) && codes.contains(code)) {
                return new FunctionResult(true);
            }

            return new FunctionResult(false);
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "DeviceLostFunction run error,codes:" + codes, e);
            return new FunctionResult(false);
        }
    }


}
