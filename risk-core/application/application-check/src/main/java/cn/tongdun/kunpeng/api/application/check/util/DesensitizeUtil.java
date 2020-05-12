package cn.tongdun.kunpeng.api.application.check.util;

import org.apache.commons.lang3.StringUtils;

public class DesensitizeUtil {

    /**
     * secret_key，保留前六位、后六位，中间二十位用星号隐藏，比如：74d08d********************edc037
     *
     * @param secretKey
     * @return
     */
    public static String secretKey(String secretKey) {
        if (StringUtils.isBlank(secretKey) || StringUtils.length(secretKey) != 32) {
            return secretKey;
        }
        return StringUtils.left(secretKey, 6).concat(
                StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(secretKey, 6),
                        StringUtils.length(secretKey), "*"), "******"));
    }
}
