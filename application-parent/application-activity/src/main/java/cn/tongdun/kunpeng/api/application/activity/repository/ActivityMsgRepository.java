package cn.tongdun.kunpeng.api.application.activity.repository;

import cn.fraudmetrix.module.kafka.object.ProducerException;
import cn.fraudmetrix.module.kafka.producer.IProducer;
import cn.fraudmetrix.module.kafka.util.ErrorHelper;
import cn.tongdun.kunpeng.api.application.activity.IActivityMsgRepository;
import cn.tongdun.kunpeng.api.application.util.CountUtil;
import cn.tongdun.kunpeng.share.config.IConfigRepository;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liang.chen
 * @Date: 2020/3/5 下午5:55
 */
@Repository
public class ActivityMsgRepository implements IActivityMsgRepository{


    private Logger logger = LoggerFactory.getLogger(ActivityMsgRepository.class);

    private static final String KUNPENG_API_RAW_ACTIVITY = "kunpeng_api_raw_activity";

    @Autowired
    private IProducer activityKafkaProducerService;

    @Autowired
    private IConfigRepository configRepository;

    //topic+seq_id -> 失败数
    private Cache<String, Integer> failedMessageCounterCache;


    @PostConstruct
    public void init(){
        //设置缓存有效期10分钟
        CacheBuilder<String, Integer> cacheBuilder = (CacheBuilder) CacheBuilder.newBuilder();
        cacheBuilder.expireAfterWrite(10, TimeUnit.MINUTES);
        failedMessageCounterCache = cacheBuilder.build();
    }

    /**
     * 消息发送
     * @param messageKey
     * @param message
     */@Override
    public void sendRawActivity(String messageKey, final String message) {

        String topic = KUNPENG_API_RAW_ACTIVITY;
        try {
            activityKafkaProducerService.produce(topic, messageKey, message.getBytes("UTF-8"), new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e == null) {
                        if (configRepository.getBooleanProperty("kafka.print.detail")) {
                            logger.info("send kafka topic ok: {}, data: {}", topic, message);
                        }
                        return;
                    }
                    if (ErrorHelper.isRecoverable(e)) {
                        //重试
                        addRedoException(topic, messageKey,message, e);
                    } else {
                        logger.error("kafka produce error, can't recover, topic:{}, seqId:{}", topic,messageKey,e);
                    }
                }
            });
        } catch (ProducerException e) {
            logger.error("kafka produce throw ProducerException, topic:{}, seqId:{}", topic,messageKey,e);
        } catch (UnsupportedEncodingException e) {
            logger.error("kafka produce throw UnsupportedEncodingException, topic:{}, seqId:{}", topic,messageKey,e);
        }
    }


    public void addRedoException(String topic, String messageKey,String message, Exception exception) {

        if (exception instanceof RecordTooLargeException) {
            logger.error("kafka produce too large message, topic:{}, seq_id:{}, size:{}",topic,messageKey,message.length());
            return;
        }

        //重试三次仍失败，就不重试了
        if (CountUtil.isBeyond(failedMessageCounterCache, topic + messageKey, 3)) {
            failedMessageCounterCache.invalidate(topic + messageKey);
            logger.error("kafka produce message try 3 times also failed, topic:{}, seq_id:{}", topic, messageKey, exception);
            return;
        }
        CountUtil.increase(failedMessageCounterCache, topic + messageKey);
    }
}
