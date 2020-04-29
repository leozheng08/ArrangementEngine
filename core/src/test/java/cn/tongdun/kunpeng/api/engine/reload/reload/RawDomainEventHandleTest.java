package cn.tongdun.kunpeng.api.engine.reload.reload;

import cn.fraudmetrix.module.kafka.producer.IProducer;
import cn.tongdun.kunpeng.api.AppMain;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.api.engine.reload.RawDomainEventHandle;
import com.alibaba.dubbo.common.json.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/4/27 下午4:57
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppMain.class)
public class RawDomainEventHandleTest {

    @Autowired
    private RawDomainEventHandle rawDomainEventHandle;

    @Test
    public void test(){
        rawDomainEventHandle.handleRawMessage(getRawMessage());
    }


    private HashMap getRawMessage(){
        try {
            String json = "{\"eventType\":\"deactivate\",\"data\":[{\"uuid\":\"4cf26923355d43e4951970c8f93210b8\"}],\"occurredTime\":1586433141469,\"entity\":\"policy_definition\"}";

            return JSON.parse(json, HashMap.class);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
