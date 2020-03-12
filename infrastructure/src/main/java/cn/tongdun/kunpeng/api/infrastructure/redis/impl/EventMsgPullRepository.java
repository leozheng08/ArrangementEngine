package cn.tongdun.kunpeng.api.infrastructure.redis.impl;

import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyManager;
import cn.tongdun.kunpeng.api.engine.reload.DomainEvent;
import cn.tongdun.kunpeng.api.engine.reload.IEventMsgPullRepository;
import cn.tongdun.kunpeng.common.util.DateUtil;
import cn.tongdun.kunpeng.share.kv.IScoreValue;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2020/3/11 下午6:42
 */
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

        //对
        return deduplicationd(scoreValueSet);
    }


    /**
     * 按业务去重
     */
    private List<String> deduplicationd(Collection<IScoreValue> scoreValueSet){

        if(scoreValueSet == null || scoreValueSet.isEmpty()){
            return new ArrayList<>();
        }

        Map<String,IScoreValue> target = new HashMap<>();

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
                String key = buildKey(eventType,entity,uuid);

                IScoreValue oldScoreValue = target.get(key);
                if(oldScoreValue == null || oldScoreValue.getScore()<occurredTime){
                    JSONObject targetJson = new JSONObject();
                    targetJson.put("eventType",eventType);
                    targetJson.put("entity",entity);
                    targetJson.put("occurredTime",occurredTime);
                    JSONArray targetDatas = new JSONArray();
                    targetDatas.add(data);
                    targetJson.put("data",targetDatas);

                    target.put(key, new IScoreValue() {
                        @Override
                        public Double getScore() {
                            return occurredTime.doubleValue();
                        }

                        @Override
                        public String getKey() {
                            return key;
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



    private static String buildKey(String eventType, String entity, String uuid) {
        return StringUtils.join(eventType, SPLIT_CHAR, entity, SPLIT_CHAR, uuid);
    }
}
