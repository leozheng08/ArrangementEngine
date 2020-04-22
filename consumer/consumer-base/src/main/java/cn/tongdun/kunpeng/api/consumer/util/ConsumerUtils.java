package cn.tongdun.kunpeng.api.consumer.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;

public class ConsumerUtils {
    private static final Logger logger                            = LoggerFactory.getLogger(ConsumerUtils.class);

    public static String md5(String str) {
        String md5 = null;
        try {
            md5 = DigestUtils.md5DigestAsHex(str.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("md5加密失败");
        }
        return md5;
    }

    public static byte[] getByte(String data) {
        byte[] bytes = null;
        try {
            bytes = data.getBytes("utf-8");
        } catch (Exception e) {
            logger.error("转字节异常，原数据为：{}", data, e);
        }
        return bytes;
    }

    public static Integer parseInt(String value) {
        try {
            if (StringUtils.isBlank(value)) {
                return null;
            }
            return Integer.parseInt(value);
        } catch (Exception e) {
            logger.error("字符串转整形失败：{}", value, e);
        }
        return null;
    }

}
