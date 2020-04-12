package cn.tongdun.kunpeng.api.application.activity.repository;

import cn.fraudmetrix.module.kafka.object.ProducerException;
import cn.fraudmetrix.module.kafka.producer.IProducer;
import cn.fraudmetrix.module.kafka.util.ErrorHelper;
import cn.tongdun.kunpeng.api.application.activity.IMsgProducer;
import cn.tongdun.kunpeng.api.application.util.CountUtil;
import cn.tongdun.kunpeng.share.config.IConfigRepository;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.tdframework.core.metrics.IMetrics;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: liang.chen
 * @Date: 2020/3/5 下午5:55
 */
@Repository
public class MsgProducerImpl implements IMsgProducer {


    private Logger logger = LoggerFactory.getLogger(MsgProducerImpl.class);

    @Autowired
    private IProducer activityKafkaProducerService;

    @Autowired
    private IConfigRepository configRepository;

    //topic+seq_id -> 失败数
    private Map<String, Integer> failedMessageCounter = new ConcurrentHashMap<>();

    private SetMultimap<String, String> failedTopicMessages = MultimapBuilder.hashKeys().hashSetValues().build();

    private Random random = new SecureRandom();

    @Autowired
    private IMetrics metrics;

    /**
     * 消息发送
     * @param message
     */
    @Override
    public void produce(final String topic,final String message) {

        try {
            final String messageKey = getKafkaKey();
            activityKafkaProducerService.produce(topic, messageKey, message.getBytes("UTF-8"), new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e == null) {
                        if (configRepository.getBooleanProperty("kafka.print.detail")) {
                            logger.info("send kafka topic ok: {}, data: {}", topic, message);
                        }
                        metrics.counter("kafka.sent.success");
                        return;
                    }
                    if (ErrorHelper.isRecoverable(e)) {
                        //重试
                        addRedoException(topic,message, e);
                    } else {
                        metrics.counter("kafka.sent.error");
                        logger.error("kafka produce error, can't recover, topic:{}, message:{}", topic,message,e);
                    }
                }
            });
        } catch (ProducerException e) {
            metrics.counter("kafka.sent.error");
            logger.error("kafka produce throw ProducerException, topic:{}, message:{}", topic,message,e);
        } catch (UnsupportedEncodingException e) {
            metrics.counter("kafka.sent.error");
            logger.error("kafka produce throw UnsupportedEncodingException, topic:{}, message:{}", topic,message,e);
        }
    }
    public void addRedoException(String topic, String message, Exception exception) {
        String seqId = getSeqId(message);

        if (exception instanceof RecordTooLargeException) {
            logger.error("kafka produce too large message, topic:{}, seq_id:{}, size:{}",topic,seqId,message.length());
            metrics.counter("kafka.sent.error");
            return;
        }

        //重试三次仍失败，就不重试了
        if (CountUtil.isBeyond(failedMessageCounter, topic + seqId, 3)) {
            failedMessageCounter.remove(topic + seqId);
            logger.error("kafka produce message try 3 times also failed, topic:{}, seq_id:{}", topic, seqId, exception);
            metrics.counter("kafka.sent.error");
            return;
        }
        CountUtil.increase(failedMessageCounter, topic + seqId);
        //将错误的消息置入缓存
        failedTopicMessages.put(topic, message);
    }


    @Scheduled(cron = " 0 */1 * * * ? ")
    public void resendFailedMessage() {
        try {
            boolean messagesEmpty = failedTopicMessages.isEmpty();
            if (messagesEmpty) {
                return;
            }
            //重新拷贝一遍
            SetMultimap<String, String> copyFailedTopicMessages = copyOld();
            Set<String> topics = copyFailedTopicMessages.keySet();
            if (topics.isEmpty()) {
                return;
            }
            int total = 0;

            Iterator<String> topicIterator = topics.iterator();
            while (topicIterator.hasNext()) {
                String topic = topicIterator.next();
                Set<String> messages = copyFailedTopicMessages.get(topic);
                if (CollectionUtils.isEmpty(messages)) {
                    continue;
                }
                Iterator<String> iterator = messages.iterator();
                while (iterator.hasNext()) {
                    String message = iterator.next();
                    produce(topic,message);
                    total += 1;
                }
            }
            //处理结果
            logger.info("kafka producer resend failed message, total:{}", total);
        } catch (Exception e) {
            logger.error( "kafka producer resend task failed", e);
        }
    }

    private synchronized SetMultimap<String, String> copyOld() {
        SetMultimap<String, String> oldOne =failedTopicMessages;
        SetMultimap<String, String> newOne = MultimapBuilder.hashKeys().hashSetValues().build();
        failedTopicMessages = newOne;
        return oldOne;
    }

    public String getKafkaKey() {
        return Long.toString(random.nextLong());
    }


    public String getSeqId(String message) {
        try {
            Map data = JSON.parseObject(message, HashMap.class);
            String seqId =  (String)data.get("sequenceId");
            if (seqId == null) {
                seqId = (String)data.get("seq_id");
                if (seqId == null) {
                    seqId = "none";
                }
            }
            return seqId;
        } catch (Exception e) {
            return "none";
        }
    }

}
