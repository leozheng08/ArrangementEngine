package cn.tongdun.kunpeng.api.application.activity.repository;

import cn.fraudmetrix.module.kafka.object.ProducerException;
import cn.fraudmetrix.module.kafka.producer.IProducer;
import cn.fraudmetrix.module.kafka.util.ErrorHelper;
import cn.tongdun.kunpeng.api.application.activity.IMsgProducer;
import cn.tongdun.kunpeng.api.application.util.CountUtil;
import cn.tongdun.kunpeng.share.config.IConfigRepository;
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
    //发送失败的消息
    private SetMultimap<String, Msg> failedTopicMessages = MultimapBuilder.hashKeys().hashSetValues().build();

    @Autowired
    private IMetrics metrics;

    /**
     * 消息发送
     * @param message
     */
    @Override
    public void produce(final String topic,final String messageKey,final String message) {

        try {
            activityKafkaProducerService.produce(topic, messageKey, message.getBytes("UTF-8"), new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e == null) {
                        if (configRepository.getBooleanProperty("kafka.print.detail")) {
                            logger.info("send kafka topic ok: {}, {}, data: {}", topic, messageKey, message);
                        }
                        failedMessageCounter.remove(topic + messageKey);
                        metrics.counter("kafka.sent.success");
                        return;
                    }
                    if (ErrorHelper.isRecoverable(e)) {
                        //重试
                        addRedoException(topic,messageKey,message, e);
                    } else {
                        metrics.counter("kafka.sent.error");
                        logger.error("kafka produce error, can't recover, topic:{}, seq_id:{}", topic, messageKey, e);
                    }
                }
            });
        } catch (ProducerException e) {
            metrics.counter("kafka.sent.error");
            logger.error("kafka produce throw ProducerException, topic:{}, seqId:{}", topic,messageKey,e);
        } catch (UnsupportedEncodingException e) {
            metrics.counter("kafka.sent.error");
            logger.error("kafka produce throw UnsupportedEncodingException, topic:{}, seqId:{}", topic,message,e);
        }
    }
    public void addRedoException(String topic, String seqId, String message, Exception exception) {
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
        failedTopicMessages.put(topic, new Msg(seqId, message));
    }


    /***
     * 发送失败消息
     */
    @Scheduled(cron = " 0 */1 * * * ? ")
    public void resendFailedMessage() {
        try {
            boolean messagesEmpty = failedTopicMessages.isEmpty();
            if (messagesEmpty) {
                return;
            }
            //重新拷贝一遍
            SetMultimap<String, Msg> copyFailedTopicMessages = copyOld();
            Set<String> topics = copyFailedTopicMessages.keySet();
            if (topics.isEmpty()) {
                return;
            }
            int total = 0;

            Iterator<String> topicIterator = topics.iterator();
            while (topicIterator.hasNext()) {
                String topic = topicIterator.next();
                Set<Msg> messages = copyFailedTopicMessages.get(topic);
                if (CollectionUtils.isEmpty(messages)) {
                    continue;
                }
                Iterator<Msg> iterator = messages.iterator();
                while (iterator.hasNext()) {
                    Msg msg = iterator.next();
                    produce(topic,msg.getKey(),msg.getMessage());
                    total += 1;
                }
            }
            //处理结果
            logger.info("kafka producer resend failed message, total:{}", total);
        } catch (Exception e) {
            logger.error( "kafka producer resend task failed", e);
        }
    }

    private synchronized SetMultimap<String, Msg> copyOld() {
        SetMultimap<String, Msg> oldOne =failedTopicMessages;
        SetMultimap<String, Msg> newOne = MultimapBuilder.hashKeys().hashSetValues().build();
        failedTopicMessages = newOne;
        return oldOne;
    }

    class Msg{
        String key;
        String message;

        public Msg(String key,String message){
            this.key = key;
            this.message = message;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
