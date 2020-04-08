package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.common.util.KunpengStringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: liuq
 * @Date: 2020/4/8 6:31 PM
 */
public class CamelAndUnderlineConvertUtil {

    /**
     * 骆峰转下划线的字段缓存
     */
    private static Map<String, String> camel2underlineCache = new ConcurrentHashMap<>(1000);
    private static Map<String, String> underline2camelCache = new ConcurrentHashMap<>(1000);

    /**
     * 把驼峰命名的字符转成下划线的方式，比如ipAddress->ip_address
     *
     * @param str
     */
    public static String camel2underline(String str) {
        if(str == null){
            return null;
        }
        String result = camel2underlineCache.get(str);
        if(result != null){
            return result;
        }

        result = KunpengStringUtils.camel2underline(str);
        if(result != null){
            camel2underlineCache.put(str,result);
        }
        return result;
    }

    public static String underline2camel(String str) {
        if(str == null){
            return null;
        }

        String result = underline2camelCache.get(str);
        if(result != null){
            return result;
        }

        result = KunpengStringUtils.underline2camel(str);
        if(result != null){
            underline2camelCache.put(str,result);
        }
        return result;
    }
}
