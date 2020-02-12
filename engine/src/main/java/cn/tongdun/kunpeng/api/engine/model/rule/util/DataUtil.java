package cn.tongdun.kunpeng.api.engine.model.rule.util;

public class DataUtil {



    /**
     * 将数据转为boolean，如果转换失败，则返回false
     * <p>
     * 只有true,Boolean.TRUE,“true",返回true
     * 其它情况都返回false(包括null)
     */
    public static boolean toBoolean(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            return Boolean.valueOf(obj.toString());
        } catch (Exception e) {
            return false;
        }
    }
}
