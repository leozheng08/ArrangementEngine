package cn.tongdun.kunpeng.api.basedata.util;

import cn.tongdun.kunpeng.api.basedata.constant.FpReasonCodeEnum;

import java.util.Map;

/**
 * @Author: liuq
 * @Date: 2020/5/21 1:57 下午
 */
public class FpReasonUtils {

    public static void put(Map<String,Object> map, FpReasonCodeEnum fpEnum){
        if(fpEnum != null){
            String[] contents = fpEnum.toString().split(":");
            put(map,contents[0],contents[1]);
        }
    }

    public static void put(Map<String,Object> map,String code,String message){
        if(map == null){
            throw new NullPointerException();
        }
        map.put("success", false);
        map.put("code",code);
        map.put("message",message);
    }

    public static void put(Map<String,Object> map,String code,String message,Map<String,Object> result){
        if(map == null){
            throw new NullPointerException();
        }
        map.put("success", false);
        map.put("code",code);
        map.put("message",message);
        if(result != null && !result.isEmpty()){
            map.put("result",result);
        }
    }

    public static void resetToTrue(Map<String, Object> map) {
        map.put("success", true);
        map.remove("code");
        map.remove("message");
    }

}
