package cn.tongdun.kunpeng.api.acl.impl.event.notify.kafka;

import cn.fraudmetrix.module.kafka.consumer.IConsumer;
import cn.fraudmetrix.module.kafka.object.RetryLaterException;
import cn.tongdun.kunpeng.share.config.DynamicConfigRepository;
import cn.tongdun.kunpeng.share.json.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仅适用于以下topic的消费者，其它topic请自行实现IConsumer接口 forseti_api_raw_activity forseti_api_raw_activity2
 * forseti_api_challenge_activity forseti_api_challenge_activity2 try_forseti_raw_activity try_forseti_raw_activity2
 */
public abstract class AbstractConsumer implements IConsumer {

    private static final Charset  UTF8                              = Charset.forName("utf8");
    private static final Logger   logger                            = LoggerFactory.getLogger(AbstractConsumer.class);


    @Autowired
    private DynamicConfigRepository dynamicConfigRepository;

    @Override
    public void doConsume(List<ConsumerRecord<String, byte[]>> messages) {
        if (messages == null) {
            return;
        }
        String clsName = this.getClass().getSimpleName();
        String topic = "";
        List<Map> bulkMessages = new ArrayList<>();
        for (ConsumerRecord<String, byte[]> record : messages) {
            String message = new String(record.value(), UTF8);
            try {
                topic = record.topic();
                Map msgItem = JSON.parseObject(message, HashMap.class);
                if (msgItem == null) {
                    logger.warn("{} Receive a blank message for {} topic, message detail:{}", clsName, topic, message);
                    continue;
                }

                if (batchConsume()) {
                    bulkMessages.add(msgItem);
                } else {
                    onMessage(topic, msgItem);
                }
            } catch (Exception e) {
                logger.warn("{} Receive a not json message for {}, data:{}", clsName, record.topic(), message,e);
            }
        }
        if (!batchConsume() || bulkMessages.isEmpty() || StringUtils.isBlank(topic)) {
            return;
        }

        try {
            onBulkMessage(topic, bulkMessages);
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                // 空指针了
                logger.warn("消费出现空指针: \n {}", JSON.toJSONString(bulkMessages, true));
            } else {
                logger.error("doConsume error:", e);
            }
            throw new RetryLaterException();
        }
    }



    // 如果需要批量消费，请实现此方法
    public void onBulkMessage(String topic, List<Map> messages) {
        throw new RuntimeException("批量消费，请实现onBulkMessage方法");
    }

    // 如果需要单一消费，请实现此方法
    public void onMessage(String topic, Map message) {
        throw new RuntimeException("单一消费，请实现onMessage方法");
    }

    /**
     * 消费模式
     */
    protected abstract boolean batchConsume();

    protected void sleep() {
        try {
            long m = dynamicConfigRepository.getLongProperty("consumer.sleep.time",100);
            logger.info("task deal failed, sleep {} ms", m);
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
