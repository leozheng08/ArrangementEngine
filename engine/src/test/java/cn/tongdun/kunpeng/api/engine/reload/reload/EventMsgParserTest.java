package cn.tongdun.kunpeng.api.engine.reload.reload;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

    /**
     * Method: parse(String event)
     */
    @Test
    public void testParse() throws Exception {
        EventMsgParser parser = new EventMsgParser();
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
                "      \"appName\": \"ios\"\n" +
                "  }\n" +
                "  ]\n" +
                "}";
        DomainEvent domainEvent = parser.parse(event);
    }


} 
