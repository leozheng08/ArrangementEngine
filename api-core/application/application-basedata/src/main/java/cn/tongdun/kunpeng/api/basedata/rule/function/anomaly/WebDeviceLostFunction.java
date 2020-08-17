package cn.tongdun.kunpeng.api.basedata.rule.function.anomaly;

import cn.tongdun.kunpeng.api.basedata.rule.function.AbstractDeviceLostFunction;
import cn.tongdun.kunpeng.api.common.Constant;

public class WebDeviceLostFunction extends AbstractDeviceLostFunction {


    @Override
    public String getAppType() {
        return "web";
    }

    @Override
    public String getName() {
        return Constant.Function.ANOMALY_DEVICE;
    }
}
