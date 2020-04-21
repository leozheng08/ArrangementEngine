package cn.tongdun.kunpeng.api.acl.event.notice;

import cn.tongdun.kunpeng.api.common.data.DomainEventTypeEnum;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import com.google.common.collect.ArrayListMultimap;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2020/4/10 上午10:43
 */
public abstract class AbstractDomainEventRepository implements IDomainEventRepository{

    //不做拆分的批量动作
    protected static final ArrayListMultimap<String,String> batchMap = ArrayListMultimap.create();

    //取最近几分钟数据
    protected static final int LAST_MINUTES = 3;

    static{
        batchMap.put("rule", DomainEventTypeEnum.BATCH_ACTIVATE.name().toLowerCase());
        batchMap.put("rule", DomainEventTypeEnum.BATCH_DEACTIVATE.name().toLowerCase());
        batchMap.put("rule", DomainEventTypeEnum.SORT.name().toLowerCase());
    }

    protected static final String KEY_EVENT_UUID = "uuid";
    protected static final String KEY_EVENT_TYPE = "eventType";
    protected static final String KEY_ENTITY = "entity";
    protected static final String KEY_OCCURRED_TIME = "occurredTime";
    protected static final String KEY_DATA = "data";
    protected static final String KEY_ENTITY_UUID = "uuid";


    /**
     * 按业务去重,只保留一个对象的uuid最新时间的变更,
     * 除batchMap里面的实体，其他原先一个消息里面data有多条记录变成一个记录一条消息
     */
    protected List<String> deduplicationd(List<String> eventList){

        if(eventList == null || eventList.isEmpty()){
            return new ArrayList<>();
        }

        Map<String,Map> target = new LinkedHashMap<>();

        for(String eventMsg :eventList) {
            Map jsonObject = JSON.parseObject(eventMsg,HashMap.class);
            List<Map> jsonArray = (List<Map>)jsonObject.get(KEY_DATA);
            if(jsonArray == null || jsonArray.isEmpty()){
                continue;
            }

            String eventUuid = JsonUtil.getString(jsonObject,KEY_EVENT_UUID);
            String eventType = JsonUtil.getString(jsonObject,KEY_EVENT_TYPE);
            String entity = JsonUtil.getString(jsonObject,KEY_ENTITY);
            Long occurredTime = JsonUtil.getLong(jsonObject,KEY_OCCURRED_TIME);


            List<String> batchEventList =  batchMap.get(entity);
            boolean isBatchEvent = false;
            //不做拆分的批量动作
            for(String batchEvent:batchEventList){
                if(eventType.endsWith(batchEvent)){
                    String uuid = JsonUtil.getString(((Map)jsonArray.get(0)),KEY_ENTITY_UUID);
                    target.put(uuid+"_batch", jsonObject);
                    isBatchEvent = true;
                    break;
                }
            }
            if(isBatchEvent){
                continue;
            }


            for(Map data :jsonArray) {
                String uuid = JsonUtil.getString(data,KEY_ENTITY_UUID);
                Map oldJsonObject = target.get(uuid);
                if(oldJsonObject == null || JsonUtil.getLong(oldJsonObject,KEY_OCCURRED_TIME)<occurredTime){
                    Map targetJson = new HashMap();
                    targetJson.put(KEY_EVENT_UUID,eventUuid);
                    targetJson.put(KEY_EVENT_TYPE,eventType);
                    targetJson.put(KEY_ENTITY,entity);
                    targetJson.put(KEY_OCCURRED_TIME,occurredTime);
                    List targetDatas = new ArrayList();
                    targetDatas.add(data);
                    targetJson.put(KEY_DATA,targetDatas);

                    target.put(uuid, targetJson);
                }
            }
        }

        return target.values().stream().map(jsonObject ->{
            return JSON.toJSONString(jsonObject);
        }).collect(Collectors.toList());
    }

}
