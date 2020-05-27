package cn.tongdun.kunpeng.api.basedata.rule.function.ios;

import cn.tongdun.kunpeng.api.basedata.rule.function.AbstractDeviceStatusAbnormalFunction;
import cn.tongdun.kunpeng.api.common.Constant;

/**
 * @Author: liuq
 * @Date: 2020/5/27 2:53 下午
 */
public class IosDeviceStatusAbnormalFunction extends AbstractDeviceStatusAbnormalFunction {

    @Override
    public String getAppType() {
        return "ios";
    }

    @Override
    public String getName() {
        return Constant.Function.IOS_DEVICE_STATUS_ABNORMAL;
    }
}
