package cn.tongdun.kunpeng.common.util;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by caipeichao on 2015/1/15.
 */
public class KunpengStringUtils {

    public static boolean equalsWithAny(final CharSequence string, final CharSequence... searchStrings) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(string) || ArrayUtils.isEmpty(searchStrings)) {
            return false;
        }
        for (final CharSequence searchString : searchStrings) {
            if (org.apache.commons.lang3.StringUtils.equals(string, searchString)) {
                return true;
            }
        }
        return false;
    }

    public static String upperCaseFirstChar(String x) {
        if (x == null) return null;
        if (x.isEmpty()) return "";
        String firstChar = x.substring(0, 1);
        String exceptFirst = x.substring(1);
        return firstChar.toUpperCase() + exceptFirst;
    }

    public static String lowerCaseFirstChar(String x) {
        if (x == null) return null;
        if (x.isEmpty()) return "";
        String firstChar = x.substring(0, 1);
        String exceptFirst = x.substring(1);
        return firstChar.toLowerCase() + exceptFirst;
    }

    /**
     * 生成随机字符串，用于生成随机密码或验证码
     * 
     * @param length
     * @param onlyNumber
     * @return
     */
    public static String generateRandomStr(int length, boolean onlyNumber) {
        StringBuffer sb = new StringBuffer();
        String[] condicateChars = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f",
                "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
                "V", "W", "X", "Y", "Z" };
        for (int i = 0; i < length;) {
            int len = onlyNumber ? 10 : condicateChars.length;
            int index = (int) (Math.random() * len);
            String str = condicateChars[index];

            // 如果有字母和数字，把容易混淆的字符排除掉
            if (!onlyNumber
                && ("1".equals(str) || "i".equals(str) || "l".equals(str) || "0".equals(str) || "O".equals(str))) {
                continue;
            }

            sb.append(str);
            i++;
        }
        return sb.toString();
    }

    /**
     * 把驼峰命名的字符转成下划线的方式，比如ipAddress->ip_address
     * 
     * @param str
     */
    public static String camel2underline(String str) {
        StringBuffer sb = new StringBuffer();

        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                String s = str.substring(i, i + 1);

                // A-Z
                if (c >= 65 && c <= 90) {
                    sb.append("_").append(s.toLowerCase());
                } else {
                    sb.append(s);
                }
            }
        }

        return sb.toString();
    }

    public static String underline2camel(String str) {
        StringBuffer sb = new StringBuffer();

        if (str != null && str.length() > 0) {
            String[] arr = str.split("_");
            for (int i = 0; i < arr.length; i++) {
                String s = arr[i];
                if (s.length() > 0) {
                    // 首字母小写
                    if (i > 0) {
                        sb.append(s.substring(0, 1).toUpperCase());
                    } else {
                        sb.append(s.substring(0, 1));
                    }
                    sb.append(s.substring(1));
                }
            }

        }
        return sb.toString();
    }

}
