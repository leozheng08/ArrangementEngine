package cn.tongdun.kunpeng.api.infrastructure.redis.impl;


import cn.tongdun.kunpeng.common.util.TestUtil;
import cn.tongdun.kunpeng.share.kv.IScoreValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RunWith(MockitoJUnitRunner.class)
public class EventMsgPullRepositoryTest {

    @InjectMocks
    private EventMsgPullRepository ruleApplicationService;

    @Test
    public void deduplicationdTest(){

        Set<IScoreValue> scoreValueSet = getScoreValueSet();

        List<String> result = (List<String>)TestUtil.invokePrivateMethod(ruleApplicationService,"deduplicationd",
                new Class[]{Set.class},scoreValueSet);

        System.out.println(result);



    }


    private Set<IScoreValue> getScoreValueSet(){
        Set<IScoreValue> scoreValueSet = new HashSet<>();
        IScoreValue scoreValue = new IScoreValue() {
            @Override
            public Double getScore() {return null; }
            @Override
            public String getKey() { return null;}

            @Override
            public Object getValue() {
                return "{\"occurredTime\":1579074305123,\"eventType\":\"PolicyCreatedEvent\",\"entity\":\"Policy\",\"data\":[{\"uuid\":\"461e2ebb9ac14d5a8101ab030a5c028a\",\"id\":123,\"name\":\"策略集名称\",\"partnerCode\":\"demo\",\"eventType\":\"loan\",\"eventId\":\"test\",\"appName\":\"ios\"}]}";
            }
            @Override
            public long getTtl() {return 0;}
        };
        scoreValueSet.add(scoreValue);


        scoreValue = new IScoreValue() {
            @Override
            public Double getScore() {return null; }
            @Override
            public String getKey() { return null;}

            @Override
            public Object getValue() {
                return "{\"occurredTime\":1579074305123,\"eventType\":\"PolicyUpdatedEvent\",\"entity\":\"Policy\",\"data\":[{\"uuid\":\"461e2ebb9ac14d5a8101ab030a5c028a\",\"id\":123,\"name\":\"策略集名称\",\"partnerCode\":\"demo\",\"eventType\":\"loan\",\"eventId\":\"test\",\"appName\":\"ios\"},{\"uuid\":\"461e2ebb9ac14d5a8101ab030a5c1234\",\"id\":456,\"name\":\"策略集名称123\",\"partnerCode\":\"demo\",\"eventType\":\"loan\",\"eventId\":\"test\",\"appName\":\"ios\"}]}";
            }
            @Override
            public long getTtl() {return 0;}
        };
        scoreValueSet.add(scoreValue);


        scoreValue = new IScoreValue() {
            @Override
            public Double getScore() {return null; }
            @Override
            public String getKey() { return null;}

            @Override
            public Object getValue() {
                return "{\"occurredTime\":1579074300023,\"eventType\":\"PolicyUpdatedEvent\",\"entity\":\"Policy\",\"data\":[{\"uuid\":\"461e2ebb9ac14d5a8101ab030a5c028a\",\"id\":123,\"name\":\"策略集名称3\",\"partnerCode\":\"demo\",\"eventType\":\"loan\",\"eventId\":\"test\",\"appName\":\"ios\"},{\"uuid\":\"461e2ebb9ac14d5a8101ab030a5c1234\",\"id\":456,\"name\":\"策略集名称3\",\"partnerCode\":\"demo\",\"eventType\":\"loan\",\"eventId\":\"test\",\"appName\":\"ios\"}]}";
            }
            @Override
            public long getTtl() {return 0;}
        };
        scoreValueSet.add(scoreValue);



        return scoreValueSet;
    }
}
