package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;

import java.util.List;

/**
 * 设备异常详情
 * @Author: liang.chen
 * @Date: 2020/2/6 下午3:16
 */
@Data
public class DeviceLostDetail extends ConditionDetail{

    /**
     * 设备异常编码
     */
    private List<String> codes;
    private List<String> codeNames;

    public DeviceLostDetail(){
        super("device_lost");
    }
}
