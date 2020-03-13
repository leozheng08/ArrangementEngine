package cn.tongdun.kunpeng.api.infrastructure.kafka;

import cn.fraudmetrix.module.kafka.object.ProducerException;
import cn.fraudmetrix.module.kafka.producer.IProducer;
import cn.tongdun.kunpeng.api.AppMain;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Author: liang.chen
 * @Date: 2020/3/12 下午5:33
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppMain.class)
public class DomainEventTest {

    private Logger logger = LoggerFactory.getLogger(DomainEventTest.class);

    @Autowired
    private IProducer domainEventProducerService;

    private List<String> domainEventList = new ArrayList<>();

    @Before
    public void initDomainEvent(){
        String event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"PolicyDefinition\",\"data\":[{\"uuid\":\"271301cc80f7458b8c0b51bb947c7366\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"Policy\",\"data\":[{\"uuid\":\"4f974b7fc3b4477999a47ca61f9de66a\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"SubPolicy\",\"data\":[{\"uuid\":\"76875ffbf05d49baab4a584e486901a3\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"SubPolicy\",\"data\":[{\"uuid\":\"5d9f4de89cce40c9acfb789d916af597\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"Rule\",\"data\":[{\"uuid\":\"88ecd6479c0e48e4a4d44871135990d1\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"Rule\",\"data\":[{\"uuid\":\"95f3a9901e584e88b75135602f8a3327\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"Rule\",\"data\":[{\"uuid\":\"22a9e93b585911ea879214187748dcf8\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"Rule\",\"data\":[{\"uuid\":\"42194619585f11ea879214187748dcf8\"}]}";
        domainEventList.add(event);


        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"Rule\",\"data\":[{\"uuid\":\"4be7ecba586011ea879214187748dcf8\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"Rule\",\"data\":[{\"uuid\":\"39a5c576218c4a67a29b4d9484a59ac6\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"Rule\",\"data\":[{\"uuid\":\"d9b690ae1b214320adedea66777e0111\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"Rule\",\"data\":[{\"uuid\":\"d30f0be68d6b4aed968ba4775f4d946f\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"Rule\",\"data\":[{\"uuid\":\"f928e0878df841c5b361a4ce007e98fe\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"Rule\",\"data\":[{\"uuid\":\"751cf2f15c8b11ea879214187748dcf8\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"RuleConditionElement\",\"data\":[{\"uuid\":\"6f154ee2ec244ab985cd02a4b15c9af7\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"RuleConditionElement\",\"data\":[{\"uuid\":\"42194619585f11ea879214187748dcf8\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"RuleConditionElement\",\"data\":[{\"uuid\":\"95ad5e2f585a11ea879214187748dcf8\"}]}";
        domainEventList.add(event);
        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"RuleConditionElement\",\"data\":[{\"uuid\":\"d36cf93c585f11ea879214187748dcf8\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"RuleConditionElement\",\"data\":[{\"uuid\":\"62a96e55586011ea879214187748dcf8\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"RuleConditionElement\",\"data\":[{\"uuid\":\"8169a2d645314cfcb7d50a18870c78f6\"}]}";
        domainEventList.add(event);
        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"RuleConditionElement\",\"data\":[{\"uuid\":\"3afcbc66ba804c5bb9348b6e8b1c330f\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"RuleConditionElement\",\"data\":[{\"uuid\":\"154d4bb1d8d94918964e1da1ec607e27\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"RuleConditionElement\",\"data\":[{\"uuid\":\"6f90c197f2eb4f7082bcc30425710203\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"RuleConditionElement\",\"data\":[{\"uuid\":\"422da1975c9611ea879214187748dcf8\"}]}";
        domainEventList.add(event);

        event = "{\"occurredTime\":"+System.currentTimeMillis()+",\"eventType\":\"UpdatedEvent\",\"entity\":\"RuleConditionElement\",\"data\":[{\"uuid\":\"d40176ce5c9611ea879214187748dcf8\"}]}";
        domainEventList.add(event);
    }

    @Test
    public void setTestData(){
        Random random = new SecureRandom();
        String topic = "kupeng_domain_event";

        for(String message  :domainEventList) {
            String messageKey = String.valueOf(random.nextLong());
            try {
                domainEventProducerService.produce(topic, messageKey, message.getBytes("UTF-8"), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if (e == null) {
                            return;
                        }
                        logger.error("kafka produce error, can't recover, topic:{}, seqId:{}", topic, messageKey, e);
                    }
                });
            } catch (ProducerException e) {
                logger.error("kafka produce throw ProducerException, topic:{}, seqId:{}", topic, messageKey, e);
            } catch (UnsupportedEncodingException e) {
                logger.error("kafka produce throw UnsupportedEncodingException, topic:{}, seqId:{}", topic, messageKey, e);
            }
        }
    }

}
