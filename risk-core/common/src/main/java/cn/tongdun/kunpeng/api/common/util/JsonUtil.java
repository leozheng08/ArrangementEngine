package cn.tongdun.kunpeng.api.common.util;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.common.base.CaseFormat;
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
        return Double.valueOf(value.toString()).longValue();
    }

    public static Integer getInteger(Map map,String key){
        Object value = map.get(key);
        if(value == null){
            return null;
        }
        return Double.valueOf(value.toString()).intValue();
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


    public static Map<String, Object> getFlattenedInfo(String source) {
        Map<String, Object> ds = JsonFlattener.flattenAsMap(source);
        Map<String, Object> result = new HashMap<>();
        Iterator<String> it = ds.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Object val = ds.get(key);
            if (key.contains("[")) {
                //去掉所有的[*]
                String ms = key.replaceAll("\\[\\d+\\]", "");
                String msCamelName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, ms);
                Object o = result.get(msCamelName);
                if (o == null) {
                    List<? super Object> item = new ArrayList<>();
                    item.add(val);
                    result.put(msCamelName, item);
                    it.remove();
                    continue;
                }
                if (o != null && o instanceof List) {
                    List<? super Object> ls = (List) o;
                    ls.add(ds.get(key));
                }
            } else {
                result.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,key), val);
            }
        }

        return result;
    }
    public static Map<String, Object> getFlatteneInfoNoLowCase(String source) {
        Map<String, Object> ds = JsonFlattener.flattenAsMap(source);
        Map<String, Object> result = new HashMap<>();
        Iterator<String> it = ds.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Object val = ds.get(key);
            if (key.contains("[")) {
                //去掉所有的[*]
                String ms = key.replaceAll("\\[\\d+\\]", "");
                String msCamelName = ms;//CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, ms);
                Object o = result.get(msCamelName);
                if (o == null) {
                    List<? super Object> item = new ArrayList<>();
                    item.add(val);
                    result.put(msCamelName, item);
                    it.remove();
                    continue;
                }
                if (o != null && o instanceof List) {
                    List<? super Object> ls = (List) o;
                    ls.add(ds.get(key));
                }
            } else {
                result.put(key, val);
//                result.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,key), val);
            }
        }

        return result;
    }

}
