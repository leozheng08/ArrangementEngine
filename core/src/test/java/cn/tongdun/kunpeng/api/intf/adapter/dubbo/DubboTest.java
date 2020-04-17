package cn.tongdun.kunpeng.api.intf.adapter.dubbo;

import cn.tongdun.kunpeng.api.AppMain;
import cn.tongdun.kunpeng.client.api.IRiskService;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author: liang.chen
 * @Date: 2020/4/15 下午8:07
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppMain.class)
public class DubboTest {

    @Resource(name = "riskServiceClient")
    private IRiskService riskServiceClient;

    @Test
    public void test(){
        RiskRequest riskRequest = new RiskRequest();
        riskRequest.setPartnerCode("demo");
        riskRequest.setEventId("cltest0327_3");
        riskRequest.setFieldValue("registerIp","123");
        IRiskResponse riskResponse = riskServiceClient.riskService(riskRequest);
        System.out.println(riskResponse);
    }

}
