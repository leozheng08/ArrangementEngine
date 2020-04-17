package cn.tongdun.kunpeng.api.application.step.ext.response;

import cn.tongdun.kunpeng.client.data.impl.camel.RiskResponse;
import cn.tongdun.kunpeng.share.json.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

/**
 * @Author: liang.chen
 * @Date: 2020/3/26 下午8:42
 */
public class RiskResponseTest {

    @Test
    public void test(){
        RiskResponse riskResponse = new RiskResponse();
        riskResponse.setPolicyName("abc");
        riskResponse.setSeqId("");
//        System.out.println(JSON.toJSONString(riskResponse));

        //过滤为null的属性
        JSON.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JSON.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY);


        System.out.println(JSON.toJSONString(riskResponse));



    }
}
