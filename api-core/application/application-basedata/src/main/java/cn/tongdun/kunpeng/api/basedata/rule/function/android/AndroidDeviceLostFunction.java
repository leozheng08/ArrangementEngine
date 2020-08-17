package cn.tongdun.kunpeng.api.basedata.rule.function.android;

import cn.tongdun.kunpeng.api.basedata.rule.function.AbstractDeviceLostFunction;
import cn.tongdun.kunpeng.api.common.Constant;

/**
 * @author: yuanhang
 * @date: 2020-08-07 13:40
 **/
public class AndroidDeviceLostFunction extends AbstractDeviceLostFunction {

    @Override
    public String getAppType() {
        return "android";
    }

    @Override
    public String getName() {
        return Constant.Function.ANDROID_DEVICE_ANOMALY;
    }
}
