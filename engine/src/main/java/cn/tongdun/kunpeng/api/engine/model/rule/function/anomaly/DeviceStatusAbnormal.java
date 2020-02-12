package cn.tongdun.kunpeng.api.engine.model.rule.function.anomaly;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.tongdun.kunpeng.api.engine.model.rule.util.DataUtil;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DeviceStatusAbnormal extends AbstractFunction {
//[
//  {
//    "name": "abnormalTags",
//    "type": "string",
//    "value": "DEVICE_INFO_TAMPERED"
//  }
//]


    private static final Logger logger = LoggerFactory.getLogger(DeviceStatusAbnormal.class);


    private String abnormalTags;


    @Override
    public String getName() {
        return Constant.Function.ANOMALY_DEVICE_STATUS_ABNORMAL;
    }

    @Override
    public void parse(List<FunctionParam> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.forEach(functionParam -> {
            if (StringUtils.equals("abnormalTags", functionParam.getName())) {
                abnormalTags = functionParam.getValue();
            }
        });
    }

    @Override
    public CalculateResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null || deviceInfo.isEmpty()) {
            return new CalculateResult(false, null);
        }

        CalculateResult result = new CalculateResult(false, null);
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
                        result = new CalculateResult(true, null);
                    }
                }
                else {
                    result = new CalculateResult(false, null);
                }
            }
            else {
                result = new CalculateResult(false, null);
            }
        }
        catch (Exception e) {
            result = new CalculateResult(false, null);
            logger.error("[Abnormality] isDeviceStatusAbnormal", e);
        }

        return result;
    }
}
