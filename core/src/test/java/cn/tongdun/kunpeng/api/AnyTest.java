package cn.tongdun.kunpeng.api;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

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

    @Test
    public void testE(){
        HttpClient client = HttpClientBuilder.create().build();
        String url = "https://sgapi.tongdun.net/riskService?partnerCode=default&secretKey=a6fbd9425f0d4990991edacc08101448&eventId=catheadcardbin&cardBin=107567&partnerKey=a5a46de3c5994504a846e08b658c0c21";
        try {
            String resp = HttpClient4Utils.sendPost(client, url, new HashMap<>(), Charset.forName("UTF-8"));
            System.out.println(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
