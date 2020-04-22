package cn.tongdun.kunpeng.api.engine.model.rule.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.*;

public class DataUtil {
    private static Logger logger = LoggerFactory.getLogger(DataUtil.class);

    /**
     * 将数据转换为Integer，如果转换失败，则返回0
     *
     * @param obj
     * @return
     */
    public static Integer toInteger(Object obj) {
        return toInteger(obj,0);
    }

    public static Integer toInteger(Object obj, Integer defVal) {
        if (obj == null) {
            return defVal;
        }
        if(defVal instanceof Integer){
            return (Integer)defVal;
        }
        try {
            return Integer.valueOf(obj.toString());
        } catch (Exception e) {
            return defVal;
        }
    }

    /**
     * 将数据转换为Long，如果转换失败，则返回0
     *
     * @param obj
     * @return
     */
    public static Long toLong(Object obj) {
        return toLong(obj,0L);
    }

    public static Long toLong(Object obj, Long defVal) {
        if (obj == null) {
            return defVal;
        }
        if(defVal instanceof Long){
            return (Long)defVal;
        }
        try {
            return Long.valueOf(obj.toString());
        } catch (Exception e) {
            return defVal;
        }
    }

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
        if(obj instanceof Boolean){
            return (Boolean)obj;
        }
        try {
            return Boolean.valueOf(obj.toString());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 将数据转换为Integer，如果转换失败，则返回0.0
     *
     * @param obj
     * @return
     */
    public static Double toDouble(Object obj) {
        return toDouble(obj,0D);
    }

    public static Double toDouble(Object obj, Double defVal) {
        if (obj == null) {
            return defVal;
        }
        if(obj instanceof Double){
            return (Double)obj;
        }
        try {
            return Double.valueOf(obj.toString());
        } catch (Exception e) {
            return defVal;
        }
    }

    /**
     * 把List 转换成String[]
     *
     * @param list
     * @return
     */
    public static String[] toStringArray(List<?> list) {
        List<String> strList = new ArrayList<String>();
        Iterator<?> originIt = list.iterator();

        Object o = null;
        while (originIt.hasNext()) {
            o = originIt.next();
            if (null != o) {
                strList.add(o.toString());
            } else {
                strList.add("");
            }

        }

        return (Arrays.copyOf(strList.toArray(), strList.size(), String[].class));
        // 或者 String[]) strList.toArray(new String[list.size()]
    }

    /**
     * 将String类型的List转换为Integer类型的List
     *
     * @param list
     * @return
     */
    public static List<Integer> toIntegerList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> intList = new ArrayList<Integer>();
        try {
            for (String element : list) {
                intList.add(Integer.parseInt(element));
            }
            return intList;
        } catch (Exception e) {
            return Collections.emptyList();
        }

    }



    public static String md5(String src) {
        if (StringUtils.isNotBlank(src)) {
            try {
                MessageDigest m = MessageDigest.getInstance("MD5");
                m.reset();
                m.update(src.getBytes(Charset.defaultCharset().name()));
                byte[] digest = m.digest();
                BigInteger bigInt = new BigInteger(1, digest);
                String hashtext = bigInt.toString(16);
                while (hashtext.length() < 32) {
                    hashtext = "0" + hashtext;
                }
                return hashtext;
            } catch (Exception e) {
                logger.error("md5异常:{}", e.getMessage());
            }
        }
        return null;
    }

    /**
     * 给一个字符串安全地加上引号。
     */
    public static String quote(String x) {
        return "\"" + StringEscapeUtils.escapeJava(x) + "\"";
    }

}
