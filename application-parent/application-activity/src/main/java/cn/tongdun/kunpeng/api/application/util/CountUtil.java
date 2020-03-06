package cn.tongdun.kunpeng.api.application.util;

import java.util.Map;

public class CountUtil {

    public static void increase(Map<String, Integer> counter, String key){
        if(counter == null || key == null){
            return;
        }
        Integer old = counter.get(key);
        if(old == null){
            counter.put(key, 1);
        }
        else{
            counter.put(key, old + 1);
        }
    }

    public static boolean isBeyond(Map<String, Integer> counter, String key, int size){
        if(counter == null || key == null){
            return false;
        }
        Integer now = counter.get(key);
        return now != null && now >= size;
    }

}
