package cn.tongdun.kunpeng.api.acl.impl.domain.event.notice.redis;



import cn.tongdun.kunpeng.api.acl.api.reload.IDomainEventRepository;
import cn.tongdun.kunpeng.api.common.data.DomainEventTypeEnum;
import cn.tongdun.kunpeng.api.common.util.DateUtil;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.kv.IScoreKVRepository;
import cn.tongdun.kunpeng.share.kv.IScoreValue;
import com.google.common.collect.ArrayListMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 从缓存或储存中拉取近几分钟的领域事件
 * 采用redis实现
 * @Author: liang.chen
 * @Date: 2020/3/11 下午6:42
 */
@Repository
public class EventMsgPullRepository implements IDomainEventRepository {

    private static Logger logger = LoggerFactory.getLogger(EventMsgPullRepository.class);

    private static final String SPLIT_CHAR = "^^";

    //不做拆分的批量动作
    private static ArrayListMultimap<String,String> batchMap = ArrayListMultimap.create();

    //取最近几分钟数据
    private static final int LAST_MINUTES = 3;

    //放到redis缓存上的超期时间
    private static final long EXPIRE_TIME = (LAST_MINUTES+1)*60*1000L;


    @Autowired
    private IScoreKVRepository scoreKVRepository;

    @PostConstruct
    public void init(){
        batchMap.put("rule", DomainEventTypeEnum.BATCH_ACTIVATE.name().toLowerCase());
        batchMap.put("rule", DomainEventTypeEnum.BATCH_DEACTIVATE.name().toLowerCase());
        batchMap.put("rule", DomainEventTypeEnum.SORT.name().toLowerCase());
    }


    @Override
    public List<String> pullLastEventMsgsFromRemoteCache(){

        Set<IScoreValue> scoreValueSet = new LinkedHashSet<>();
        try {
            for(int i=LAST_MINUTES-1;i>=0;i--){
                String lastKey = DateUtil.getLastMinute(i);
                Set<IScoreValue> scoreValueSetTmp = scoreKVRepository.zrangeByScoreWithScores(lastKey,0,Long.MAX_VALUE);
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
     * 除batchMap里面的实体，其他原先一个消息里面data有多条记录变成一个记录一条消息
     */
    private List<String> deduplicationd(Set<IScoreValue> scoreValueSet){

        if(scoreValueSet == null || scoreValueSet.isEmpty()){
            return new ArrayList<>();
        }

        Map<String,IScoreValue> target = new LinkedHashMap<>();

        for(IScoreValue scoreValue :scoreValueSet) {
            String value = scoreValue.getValue().toString();
            Map jsonObject = JSON.parseObject(value,HashMap.class);
            List<Map> jsonArray = (List<Map>)jsonObject.get("data");
            if(jsonArray == null || jsonArray.isEmpty()){
                continue;
            }

            String eventType = JsonUtil.getString(jsonObject,"eventType");
            String entity = JsonUtil.getString(jsonObject,"entity");
            Long occurredTime = JsonUtil.getLong(jsonObject,"occurredTime");


            List<String> batchEventList =  batchMap.get(entity);
            boolean isBatchEvent = false;
            //不做拆分的批量动作
            for(String batchEvent:batchEventList){
                if(eventType.endsWith(batchEvent)){
                    String uuid = JsonUtil.getString(((Map)jsonArray.get(0)),"uuid");
                    target.put(uuid+"_batch", createScoreValue(uuid,jsonObject,occurredTime));
                    isBatchEvent = true;
                    break;
                }
            }
            if(isBatchEvent){
                continue;
            }


            for(Map data :jsonArray) {
                String uuid = JsonUtil.getString(data,"uuid");
                IScoreValue oldScoreValue = target.get(uuid);
                if(oldScoreValue == null || oldScoreValue.getScore()<occurredTime){
                    Map targetJson = new HashMap();
                    targetJson.put("eventType",eventType);
                    targetJson.put("entity",entity);
                    targetJson.put("occurredTime",occurredTime);
                    List targetDatas = new ArrayList();
                    targetDatas.add(data);
                    targetJson.put("data",targetDatas);

                    target.put(uuid, createScoreValue(uuid,targetJson,occurredTime));
                }
            }
        }

        return target.values().stream().map(scoreValue ->{
            return JSON.toJSONString(scoreValue.getValue());
        }).collect(Collectors.toList());
    }


    private IScoreValue createScoreValue(String key,Object value, Long occurredTime){
        return  new IScoreValue() {
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
                return value;
            }

            @Override
            public long getTtl() {
                return 0;
            }
        };
    }

    @Override
    public void putEventMsgToRemoteCache(String eventMsg,Long occurredTime){
        String currentKey = DateUtil.getYYYYMMDDHHMMStr();
        scoreKVRepository.zadd(currentKey,occurredTime,eventMsg);
        scoreKVRepository.setTtl(currentKey,EXPIRE_TIME);
    }
}
