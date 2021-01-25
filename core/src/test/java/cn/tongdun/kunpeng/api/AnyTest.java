package cn.tongdun.kunpeng.api;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author yangchangkai
 * @date 2021/1/8
 */
public class AnyTest {
    @Test
    public void test(){
        String SALT = "TD_ANTIFRAUND_TAURUS";
        DateTimeFormatter SDF = DateTimeFormatter.ofPattern("yyMMddHHmm");
        LocalDateTime localDateTime = LocalDateTime.now();
        String token = md5(SDF.format(localDateTime) + SALT);
        long t = System.currentTimeMillis();
        System.out.println(t);

//        String url = "http://192.168.43.151:8090";
        String url = "http://10.57.242.215:8090";
        String imageUrl = "/resource/image?image_id=202101080f7c4434df4347fb9078e297abeebea2";
        System.out.println(StringUtils.join(url, imageUrl, "&", "token=", token, "&", "t=", t));
    }

    public static String md5(String str) {
        String md5 = null;
        try {
            md5 = DigestUtils.md5DigestAsHex(str.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
        }
        return md5;
    }
}
