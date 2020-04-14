package cn.tongdun.kunpeng.client.data;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/3/26 下午5:35
 */
public class RiskRequestTest {

    @Test
    public void test(){
        RiskRequest riskRequest = new RiskRequest();
        riskRequest.setSeqId("abc1231231212312");
        riskRequest.setSecretKey("29350052884f43038b41f6dbf12ace25");
        System.out.println(riskRequest.toString());
    }
}
