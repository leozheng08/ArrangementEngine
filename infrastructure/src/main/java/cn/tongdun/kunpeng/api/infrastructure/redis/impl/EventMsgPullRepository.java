package cn.tongdun.kunpeng.api.infrastructure.redis.impl;


import cn.tongdun.kunpeng.api.engine.reload.IDomainEventRepository;
import cn.tongdun.kunpeng.common.util.DateUtil;
import cn.tongdun.kunpeng.share.kv.IScoreValue;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2020/3/11 下午6:42
 */
@Repository
public class EventMsgPullRepository implements IDomainEventRepository {

    private static Logger logger = LoggerFactory.getLogger(EventMsgPullRepository.class);

    private static final String SPLIT_CHAR = "^^";

    //取最近几分钟数据
    private static final int LAST_MINUTES = 60;

    @Autowired
    private RedisScoreKVRepository redisScoreKVRepository;

    @Override
    public List<String> pullLastEventMsgsFromRemoteCache(){

        Set<IScoreValue> scoreValueSet = new LinkedHashSet<>();
        try {
            for(int i=LAST_MINUTES-1;i>=0;i--){
                String lastKey = DateUtil.getLastMinute(i);
                Set<IScoreValue> scoreValueSetTmp = redisScoreKVRepository.zrangeByScoreWithScores(lastKey,0,Long.MAX_VALUE);
                if(scoreValueSetTmp.isEmpty()){
                    continue;
                }

                scoreValueSet.addAll(scoreValueSetTmp);
            }


        } catch (Exception e) {
            logger.error("update rule form redis error!",e);
        }

        //业务去重
        return deduplicationd(scoreValueSet);
    }


    /**
     * 按业务去重,只保留一个对象的uuid最新时间的变更,
     * 并原先一个消息里面data有多条记录变成一个记录一条消息
     */
    private List<String> deduplicationd(Set<IScoreValue> scoreValueSet){

        if(scoreValueSet == null || scoreValueSet.isEmpty()){
            return new ArrayList<>();
        }

        Map<String,IScoreValue> target = new LinkedHashMap<>();

        for(IScoreValue scoreValue :scoreValueSet) {
            String value = scoreValue.getValue().toString();
            JSONObject jsonObject = JSONObject.parseObject(value);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if(jsonArray == null || jsonArray.isEmpty()){
                continue;
            }

            String eventType = jsonObject.getString("eventType");
            String entity = jsonObject.getString("entity");
            Long occurredTime = jsonObject.getLong("occurredTime");
            for(Object obj:jsonArray) {
                JSONObject data = (JSONObject)obj;
                String uuid = data.getString("uuid");

                IScoreValue oldScoreValue = target.get(uuid);
                if(oldScoreValue == null || oldScoreValue.getScore()<occurredTime){
                    JSONObject targetJson = new JSONObject();
                    targetJson.put("eventType",eventType);
                    targetJson.put("entity",entity);
                    targetJson.put("occurredTime",occurredTime);
                    JSONArray targetDatas = new JSONArray();
                    targetDatas.add(data);
                    targetJson.put("data",targetDatas);

                    target.put(uuid, new IScoreValue() {
                        @Override
                        public Double getScore() {
                            return occurredTime.doubleValue();
                        }

                        @Override
                        public String getKey() {
                            return uuid;
                        }

                        @Override
                        public Object getValue() {
                            return targetJson;
                        }

                        @Override
                        public long getTtl() {
                            return 0;
                        }
                    });
                }
            }
        }

        return target.values().stream().map(scoreValue ->{
            return scoreValue.getValue().toString();
        }).collect(Collectors.toList());
    }

    @Override
    public void putEventMsgToRemoteCache(String eventMsg,Long occurredTime){
        String currentKey = DateUtil.getYYYYMMDDHHMMStr();
        redisScoreKVRepository.zadd(currentKey,occurredTime,eventMsg);
    }
}
