package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;

/**
 * 设备模拟器类型命中详情
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class AndroidEmulatorDetail extends ConditionDetail{

    //设备模拟器类型
    private String emulatorType;

    public AndroidEmulatorDetail() {
        super("android_emulator");
    }

}
