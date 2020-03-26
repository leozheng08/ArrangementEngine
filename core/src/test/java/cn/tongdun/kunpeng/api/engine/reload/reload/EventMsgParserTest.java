package cn.tongdun.kunpeng.api.engine.reload.reload;

import cn.tongdun.kunpeng.api.engine.reload.DomainEvent;
import cn.tongdun.kunpeng.api.engine.reload.EventMsgParser;
import cn.tongdun.kunpeng.share.dataobject.RuleDO;
import cn.tongdun.kunpeng.share.json.JSON;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

/**
 * EventMsgParser Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>03/06/2020</pre>
 */
public class EventMsgParserTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }


    private String getEventJson(){
        String event = "{\n" +
                "  \"occurredTime\": 1579074305123,\n" +
                "  \"eventType\": \"PolicyCreatedEvent\",\n" +
                "  \"entity\": \"Policy\",\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"uuid\": \"461e2ebb9ac14d5a8101ab030a5c028a\",\n" +
                "      \"id\": 123,\n" +
                "      \"name\": \"策略集名称\",\n" +
                "      \"partnerCode\": \"demo\",\n" +
                "      \"eventType\": \"loan\",\n" +
                "      \"eventId\": \"test\",\n" +
                "      \"appName\": \"ios\",\n" +
                "      \"a1\": true,\n" +
                "      \"a2\": 123,\n" +
                "      \"a3\": 123456789012345678\n" +
                "  }\n" +
                "  ]\n" +
                "}";
        return event;
    }

    /**
     * Method: parse(String event)
     */
    @Test
    public void testParse() throws Exception {
        EventMsgParser parser = new EventMsgParser();

        DomainEvent domainEvent = parser.parse(getEventJson());
    }

    @Test
    public void testJson(){

        HashMap map = JSON.parseObject(getEventJson(), HashMap.class);
        System.out.println(map);
    }


    private String getRuleJson(){
        String event = "{\"template\":\"pattern/functionKit\",\"concurrencyVersion\":2,\"gmtModify\":1585137156792,\"ifRule\":false,\"riskStrategy\":{\"mode\":\"Weighted\",\"weightPropertyValue\":\"\",\"weightProperty\":\"\",\"baseWeight\":1.0,\"lowerLimitScore\":-30,\"upperLimitScore\":30,\"weightRatio\":0.0},\"subPolicyUuid\":\"23cfee5f3685469d872f802e1d0d10b0\",\"uuid\":\"b604947c29dc4f339abafb85615f4a20\",\"operator\":\"xiyu.wang@tongdun.cn\",\"ruleCustomId\":\"12462\",\"ruleType\":\"ONLINE\",\"id\":12462,\"locked\":false,\"creator\":\"xiyu.wang@tongdun.cn\",\"hasAutoAddToCustomListRecord\":false,\"policyDefinitionUuid\":\"672be6d5a5d248f3b5517f9d364a7ad1\",\"gmtCreate\":1585107021000,\"priority\":0,\"policyUuid\":\"e760d5b70059411e9699527804789be7\",\"condition\":{\"op\":\">\",\"leftProperty\":{\"name\":\"pattern/functionKit\",\"type\":\"alias\"},\"description\":\"对指标进行求和、求平均、方差、标准差、最大值、最小值、中位数、幂、指数、自然指数、对数、自然对数计算\",\"rightPropertyValue\":{\"dataType\":\"STRING\",\"type\":\"input\",\"value\":\"0\"},\"priority\":0,\"params\":[{\"dataType\":\"enum\",\"name\":\"funcType\",\"type\":\"function\",\"value\":\"VelocityFuncType.SUM\"},{\"dataType\":\"index\",\"name\":\"calVariable\",\"type\":\"indicatrix\",\"value\":\"424965533163615209\"},{\"dataType\":\"index\",\"name\":\"otherCalVariables\",\"type\":\"indicatrix\",\"value\":\"425327486381880297\"},{\"dataType\":\"double\",\"name\":\"naturalValue\",\"type\":\"input\",\"value\":\"0\"}],\"logicOperator\":\"&&\"},\"name\":\"函数工具箱\",\"pilotRun\":false}";
        return event;
    }

    @Test
    public void testRule(){
        String json = getRuleJson();
        JSON.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSON.getObjectMapper().configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        JSON.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS,false);
        JSON.getObjectMapper().configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY,false);
        JSON.getObjectMapper().configure(DeserializationFeature.WRAP_EXCEPTIONS,false);
        JSON.getObjectMapper().configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL,true);
        JSON.getObjectMapper().configure(DeserializationFeature.EAGER_DESERIALIZER_FETCH,false);


        RuleDO ruleDO = JSON.parseObject(json,RuleDO.class);
        System.out.println(ruleDO);
    }
} 
