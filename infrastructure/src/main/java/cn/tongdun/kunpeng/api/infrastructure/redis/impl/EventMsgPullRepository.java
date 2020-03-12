package cn.tongdun.kunpeng.api.infrastructure.redis.impl;


import cn.tongdun.kunpeng.api.engine.reload.IEventMsgPullRepository;
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
public class EventMsgPullRepository implements IEventMsgPullRepository{

    private static Logger logger = LoggerFactory.getLogger(EventMsgPullRepository.class);

    public static final String SPLIT_CHAR = "^^";
    @Autowired
    private RedisScoreKVRepository redisScoreKVRepository;


    @Override
    public List<String> pullLastEventMsgs(){
        String currentKey = DateUtil.getYYYYMMDDHHMMStr();
        String lastKey = DateUtil.getLastMinute();
        Set<IScoreValue> scoreValueSet = new HashSet<>();
        try {
            scoreValueSet = redisScoreKVRepository.zrangeByScoreWithScores(currentKey,0,-1);
            scoreValueSet.addAll(redisScoreKVRepository.zrangeByScoreWithScores(lastKey,0,-1));
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
}
