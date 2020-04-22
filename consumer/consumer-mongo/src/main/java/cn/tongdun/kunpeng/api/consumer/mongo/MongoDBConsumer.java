package cn.tongdun.kunpeng.api.consumer.mongo;

import cn.tongdun.kunpeng.api.consumer.base.AbstractConsumer;
import cn.tongdun.kunpeng.api.consumer.util.ConsumerUtils;
import cn.tongdun.kunpeng.api.consumer.util.JsonUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

public class MongoDBConsumer extends AbstractConsumer {
    private final static Logger logger      = LoggerFactory.getLogger(MongoDBConsumer.class);

    @Value("${es.switch}")
    private boolean esSwitch;

    @Value("${activity.es.retry.times}")
    private int retryTimes;

    @Autowired
    private IMongoDBStorage mongoDBStorage;

    @Override
    protected boolean batchConsume() {
        return true;
    }

    @Override
    public void onBulkMessage(String topic, List<Map> messages) {
        if (!esSwitch) {
            return;
        }
        for (Map json : messages) {
            String sequenceId = JsonUtil.getString(json,"sequenceId");
            // 业务保留最近1年的调用数据（注意区别事件时间(eventOccurTime)不一定是一年内）
            if (json.get("request") != null) {
                Object eventOccurTime = ((Map)json.get("request")).get("eventOccurTime");
                if (eventOccurTime instanceof Long) {
                    GregorianCalendar gc = new GregorianCalendar() {
                        {
                            setTime(new Date());
                            add(Calendar.YEAR, -1);// 减1年
                        }
                    };
                    if (gc.getTimeInMillis() > (Long) eventOccurTime) {
                        logger.warn("事件时间(eventOccurTime)超过一年,将不存储MongDB,请通过详情接口查询 sequenceId:{} topic:{} data:{}", sequenceId, topic, json);
                        continue;
                    }
                }
            }
            boolean flag = writeMongoDB(json, topic);
            if (!flag) {
                //写入失败，重试3次
                retryWrite(json, topic);
            }
        }

    }

    /**
     * 重试
     * @param json
     * @param topic
     */
    private void retryWrite(Map json, String topic) {
        String sequenceId = JsonUtil.getString(json,"sequenceId");
        boolean retryFlag = false;
        for (int time =0; time < retryTimes; time++) {
            boolean flag = writeMongoDB(json, topic);
            if (flag) {
                retryFlag = true;
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (retryFlag) {
            logger.info("数据重试写入mongdb正常，sequenceId:{} topic:{} data:{}",sequenceId, topic, json);
        } else {
            logger.warn("数据重试写入mongdb失败，sequenceId:{} topic:{} data:{}",sequenceId, topic, json);
        }
    }

    /**
     * 数据写入mongdb
     * @param json
     * @param topic
     * @return
     */
    private boolean writeMongoDB(Map json, String topic) {
        //写入mongoDB
        String sequenceId = JsonUtil.getString(json,"sequenceId");
        String encodedKey = ConsumerUtils.md5(sequenceId);
        byte[] data = ConsumerUtils.getByte(JSON.toJSONString(json));
        try {
            mongoDBStorage.set(encodedKey, data);
            logger.info("写入mongoDB数据：sequenceId {}, data {}", sequenceId, new String(mongoDBStorage.get(encodedKey), "utf-8"));
            return true;
        } catch (Exception e) {
            logger.error("数据写入mongdb异常，sequenceId:{} topic:{} data:{}",sequenceId, topic, json, e);
        }
        return false;
    }


}
