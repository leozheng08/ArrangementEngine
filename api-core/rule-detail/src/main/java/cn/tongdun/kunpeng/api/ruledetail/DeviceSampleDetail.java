package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;
import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;
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
