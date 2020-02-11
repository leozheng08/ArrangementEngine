package cn.tongdun.kunpeng.api.ruledetail;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * 设备状态异常
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class DeviceStatusAbnormalDetail extends ConditionDetail{

    private List<String> abnormalTags;

    public DeviceStatusAbnormalDetail() {
        super("device_status_abnormal");
    }

}
