package cn.tongdun.kunpeng.api.basedata.rule.function.android;

import cn.tongdun.kunpeng.api.basedata.rule.function.AbstractDeviceStatusAbnormalFunction;
import cn.tongdun.kunpeng.api.common.Constant;

/**
 * @Author: liuq
 * @Date: 2020/5/27 2:51 下午
 */
public class AndroidDeviceStatusAbnormalFunction extends AbstractDeviceStatusAbnormalFunction {

    @Override
    public String getAppType() {
        return "android";
    }

    @Override
    public String getName() {
        return Constant.Function.ANDROID_DEVICE_STATUS_ABNORMAL;
    }
}
