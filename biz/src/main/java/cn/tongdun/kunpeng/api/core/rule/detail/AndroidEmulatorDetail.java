package cn.tongdun.kunpeng.api.core.rule.detail;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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
