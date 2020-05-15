package cn.tongdun.kunpeng.api.basedata.rule.function.anomaly;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.tongdun.kunpeng.api.engine.model.rule.util.DataUtil;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class DeviceStatusAbnormalFunction extends AbstractFunction {


    private static final Logger logger = LoggerFactory.getLogger(DeviceStatusAbnormalFunction.class);


    private String abnormalTags;


    @Override
    public String getName() {
        return Constant.Function.ANOMALY_DEVICE_STATUS_ABNORMAL;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("anomaly DeviceStatusAbnormal function parse error,no params!");
        }

        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("abnormalTags", param.getName())) {
                abnormalTags = param.getValue();
            }
        });
    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null || deviceInfo.isEmpty()) {
            return new FunctionResult(false);
        }

        boolean result = false;
        try {
            boolean success = DataUtil.toBoolean(deviceInfo.get("success"));
            if (success) {
                boolean flag = true;
                Collection<String> fpAbnormalTags = (Collection<String>) deviceInfo.get("abnormalTags");
                if ((null != deviceInfo.get("fmVersion") && deviceInfo.get("fmVersion").toString().compareToIgnoreCase("3.0.0") < 0) ||
                        (null != deviceInfo.get("version") && deviceInfo.get("version").toString().compareToIgnoreCase("3.0.0") < 0) ||
                        null == fpAbnormalTags) {
                    flag = false;
                }
                if (null != fpAbnormalTags && null != deviceInfo.get("appOs") && deviceInfo.get("appOs").toString().compareToIgnoreCase("web") == 0) {
                    flag = true;
                }
                if (flag) {
                    boolean isHitFlag = false;
                    for (String tag : fpAbnormalTags) {
                        if (abnormalTags.contains(tag)) {
                            isHitFlag = true;
                        }
                    }
                    if (isHitFlag) {
                        result = true;
                    }
                }
                else {
                    result = false;
                }
            }
            else {
                result = false;
            }
        }
        catch (Exception e) {
            result = false;
            logger.error(TraceUtils.getFormatTrace()+"[Abnormality] isDeviceStatusAbnormal", e);
        }

        return new FunctionResult(result);
    }


}
