package cn.tongdun.kunpeng.api.basedata.rule.function.ios;

import cn.tongdun.kunpeng.api.basedata.rule.function.AbstractDeviceLostFunction;
import cn.tongdun.kunpeng.api.common.Constant;

/**
 * @author: yuanhang
 * @date: 2020-08-07 13:39
 **/
public class IosDeviceLostFunction extends AbstractDeviceLostFunction {
    @Override
    public String getAppType() {
        return "ios";
    }

    @Override
    public String getName() {
        return Constant.Function.IOS_DEVICE_ANOMALY;
    }
}
