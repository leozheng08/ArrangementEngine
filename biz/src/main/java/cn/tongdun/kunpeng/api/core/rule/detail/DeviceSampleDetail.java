package cn.tongdun.kunpeng.api.core.rule.detail;

import lombok.Data;

/**
 * 采样异常
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:49
 */
@Data
public class DeviceSampleDetail extends ConditionDetail {

    private boolean imageLoaded;
    private String deviceId;

    public DeviceSampleDetail(){
        super("device_sample");
    }
}
