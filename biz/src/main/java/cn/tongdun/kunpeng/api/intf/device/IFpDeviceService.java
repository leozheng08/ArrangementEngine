package cn.tongdun.kunpeng.api.intf.device;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author: liang.chen
 * @Date: 2020/1/7 上午9:56
 */
public interface IFpDeviceService {

    JSONObject query(QueryParams var1);
}
