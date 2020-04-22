package cn.tongdun.kunpeng.api.consumer.util;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class JsonUtil {

    public static String getString(Map map,String key){
        Object value = map.get(key);
        if(value == null){
            return null;
        }
        return value.toString();
    }

    public static Long getLong(Map map,String key){
        Object value = map.get(key);
        if(value == null){
            return null;
        }
        return Long.parseLong(value.toString());
    }

    public static Integer getInteger(Map map,String key){
        Object value = map.get(key);
        if(value == null){
            return null;
        }
        return Integer.parseInt(value.toString());
    }

    public static Double getDouble(Map map,String key){
        Object value = map.get(key);
        if(value == null){
            return null;
        }
        return Double.parseDouble(value.toString());
    }

    public static Boolean getBoolean(Map map,String key){
        Object value = map.get(key);
        if(value == null){
            return null;
        }
        String str = value.toString();
        if(StringUtils.isBlank(str)){
            return false;
        }
        if(str.equals("1") || str.equalsIgnoreCase("true")){
            return true;
        }
        return false;
    }



    public static Object getJsonValue(Object json, String key) {
        String[] keys = key.split("\\.");
        for (int j = 0; j < keys.length; j++) {
            if (json == null) {
                return null;
            }
            String k = keys[j];
            if (json instanceof Map) {
                json = ((Map) json).get(k);
            } else {
                return null;
            }
        }
        return json;
    }

    public static void setJsonValue(Map<String, Object> json, String key, Object value) {
        String[] keys = key.split("\\.");
        for (int i = 0; i < keys.length - 1; i++) {
            String k = keys[i];
            if (!json.containsKey(k)) {
                json.put(k, new HashMap());
            }
            json = (Map) json.get(k);
        }
        String lastKey = keys[keys.length - 1];
        if (value == null) {
            json.remove(lastKey);
        } else {
            json.put(lastKey, value);
        }
    }

    /**
     * 消除key中的点号，比如： <code>
     * {
     *     "a.b.c": "123",
     *     "a.b.d": "222"
     * }
     * </code> 转换之后就变成： <code>
     * {
     *     "a": {
     *         "b": {
     *             "c": "123",
     *             "d": "222"
     *         }
     *     }
     * }
     * </code>
     */
    public static Map parsePath(Map<String, Object> list) {
        Map result = new HashMap();
        for (String key : list.keySet()) {
            Object o = list.get(key);
            if (o instanceof Map) {
                o = parsePath((Map) o); // key必须为String类型
            }
            setJsonValue(result, key, o);
        }
        return result;
    }

}
