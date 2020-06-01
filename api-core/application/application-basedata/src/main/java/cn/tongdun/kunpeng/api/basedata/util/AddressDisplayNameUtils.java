package cn.tongdun.kunpeng.api.basedata.util;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Author: liuq
 * @Date: 2020/5/29 4:44 下午
 */
public class AddressDisplayNameUtils {

    private static final Map<String, String> map = Maps.newHashMap();

    static {
        map.put("ipAddress", "IP地理位置");
        map.put("trueIpAddress", "True IP地理位置");
        map.put("mobileAddress", "手机归属地");
        map.put("snAddress", "身份证归属地");
        map.put("binAddress", "BIN卡发卡地");
        map.put("billAddress", "账单地址");
        map.put("deliveryAddress", "收货地址");
    }

    public static String getAddressDisplayName(String address) {
        return map.get(address);
    }
}
