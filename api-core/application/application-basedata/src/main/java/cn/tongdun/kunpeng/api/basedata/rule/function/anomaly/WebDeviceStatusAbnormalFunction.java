package cn.tongdun.kunpeng.api.basedata.rule.function.anomaly;

import cn.tongdun.kunpeng.api.basedata.rule.function.AbstractDeviceStatusAbnormalFunction;
import cn.tongdun.kunpeng.api.common.Constant;

public class WebDeviceStatusAbnormalFunction extends AbstractDeviceStatusAbnormalFunction {

    @Override
    public String getName() {
        return Constant.Function.ANOMALY_DEVICE_STATUS_ABNORMAL;
    }


    @Override
    public String getAppType() {
        return "web";
    }
}
