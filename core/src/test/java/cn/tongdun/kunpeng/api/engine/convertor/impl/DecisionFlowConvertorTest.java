package cn.tongdun.kunpeng.api.engine.convertor.impl;

import cn.tongdun.kunpeng.api.AppMain;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionFlow;
import cn.tongdun.kunpeng.api.engine.util.CompressUtil;
import cn.tongdun.kunpeng.client.dto.DecisionFlowDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: liang.chen
 * @Date: 2020/3/20 下午2:32
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppMain.class)
public class DecisionFlowConvertorTest {

    @Autowired
    private DecisionFlowConvertor decisionFlowConvertor;

    String bpm = "H4sIAAAAAAAAAM1Y227bRhB991cQLJC3lci9kLty5ACpnKCAkwa5FH0z9jKrMKFIhaQsO1/Rl/YP+pjPKvIbHZKyIgmOI9kpmheJGs7Mzpwzl7UfPrqc5cEFVHVWFuMwHkRhAIUtXVZMx+Gb10+IDB+dHB09NPNZQUcOfFZkDerWARoW9aiTj8O3TTMfDYfL5XJQzqaDspoO6znY4eMXz54PaRRHkaB8+OzXyelZ2Ftu2bwzZV13Vq4qy7wON7y7bF/3k1+u7Zz9us1ksmHx89rillO2LNZnvNMXejsJFAxsORu2D6+v5rBOo9kz3ct6O4ol65RoFMXD35+dvbJvYaZJVtSNLiwgU5fzsmqgGoeoNgCbZ/MaBh0lg1npIIcKA6o2NH/7wjQdsMGTrNA5ucDskojShMQpZeSxSjuDCupW90wX04WewlZkswvI+9jaisncOJysSyMMGl1NoXmuZ1DPtYVv5N4gVDceciOgdTaqOxzOSqubLpcDii9oZTQaXNYuuCWooP/aVTPzOpv1/rsQ6iE2TNBLUTU8OQqCVafMq9Iifh00jgojTQpxqjm3aaIM05R5p53ScSoZAlifXoJdNNrkCEFTLZCyAuEbh9Uihyd5uQyDlccWh3H4osoudINaWFojxPg9Yve8M8AW1Yu8edHLupDWQWHdVM3pBRTNdQ/23L1ay8+VA6kMlwSUU4THmhLDICICf3rPtNI0Wjlduy0XzbTEgXHyCj4scHp0AZ+nibfCpo54yRzhVkmibcJIBIqlwrokMerhcMdDH+1wN9ytLODS5os6u4CniMBSX23ncrrz9pxz5nnkDdGYDeHUR0T5NCVG6hSMoYYJHwbTXnuSVWD7qpqgj2qKQYXBClMEajNBSGIaW6uITGhMuNboUyRAYqsg9YonyrNdqDKcrbN7QbX2sA8H+4R4MwffcKx9alRbHEIlCKpMOFHMAGGRlkAtcKH97eTusrhFscE3BRb7S6z+17p+v03x452358xFzjsjSSI14ocpEhk5SpiU3AuWIIS8b5Vrx61Z3y//fPrj86e/Pv/594O8OR6a6sG0OW4fcXIVQd1cYUM++LAom2NfFg2ps48wiun88tiWeVmNflJKHS/LyhFTgX4/6j6JzvPepnXWe6ed+9ZpK+uDue7tp1W5mI9DDcizZCyGNOI+4YYrLFyeCu8T6rTdr5YOofygWpKSCSERWpXQCPtI4ZMChn2U2NQKBU7Gt1O+y+p9KI/jlEEcRyTSsWvT1BgNNURRZ4SWsbBC/M+Ux9+mPPZUKGmoVVxznQijDe4Kw0FwJkDS/Sg/pBkPolxz9OsZI+CRbW4Mjk7TVpV3HmIGzmp9D8qhcDcso9OV9NwzEUdCAJEO8MzUGiI9Z5gW0htZm3of7wfQIaX7FYC+gvwBAG07Xo/BVbbbe3rjjJ1NfejaCIO6XFQWXoI/fM+vLnKd7R33alv0zbrzVveVKiurrLnC+u8Ftixcf6/50iVvFm2+4cn6MrCR+V3A2mtbb4J114S/QHaPPXUAZO3T9b17CzvmqOeeRZT7lHucWlLb2IlYMJngvLHfD9t9BtB/je3+C+F7YKssg4QnJjGIMbegRItyyrT3UWoj+H7Y7jO7trC9a9VttPpBE/jHafF9pvHtUO1ZRD8eVNfy1R+JJ0fXgo3/2Jwc/Qte9C+Q7hEAAA==";

    @Test
    public void test(){
        try {
            String processXml = CompressUtil.ungzip(bpm);

            DecisionFlowDTO decisionFlowDTO = new DecisionFlowDTO();
            decisionFlowDTO.setProcessContent(processXml);
            DecisionFlow decisionFlow = decisionFlowConvertor.convert(decisionFlowDTO,true);
            Assert.assertNotNull(decisionFlow.getGraph());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
