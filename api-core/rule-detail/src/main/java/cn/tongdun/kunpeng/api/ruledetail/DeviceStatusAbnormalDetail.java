package cn.tongdun.kunpeng.api.ruledetail;

import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;
import lombok.Data;

import java.util.List;

/**
 * 设备状态异常
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class DeviceStatusAbnormalDetail extends ConditionDetail {

    /**
     * 异常状态码
     */
    private List<String> abnormalTags;
    /**
     * 对应中文显示
     */
    private List<String> abnormalTagNames;

    public DeviceStatusAbnormalDetail() {
        super("device_status_abnormal");
    }

}
