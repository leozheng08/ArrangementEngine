package cn.tongdun.kunpeng.api.acl.impl.event.notice.db;

import cn.tongdun.kunpeng.api.acl.api.event.notice.IDomainEventRepository;
import cn.tongdun.kunpeng.api.common.data.DomainEventTypeEnum;
import cn.tongdun.kunpeng.api.common.util.DateUtil;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.DomainEventDAO;
import cn.tongdun.kunpeng.share.dataobject.DomainEventDO;
import cn.tongdun.kunpeng.share.json.JSON;
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
public class DbDomainEventRepository implements IDomainEventRepository {

    private static Logger logger = LoggerFactory.getLogger(DbDomainEventRepository.class);

    private static final String SPLIT_CHAR = "^^";

    //不做拆分的批量动作
    private static ArrayListMultimap<String,String> batchMap = ArrayListMultimap.create();

    //取最近几分钟数据
    private static final int LAST_MINUTES = 3;


    @Autowired
    private DomainEventDAO domainEventDAO;

    @PostConstruct
    public void init(){
        batchMap.put("rule", DomainEventTypeEnum.BATCH_ACTIVATE.name().toLowerCase());
        batchMap.put("rule", DomainEventTypeEnum.BATCH_DEACTIVATE.name().toLowerCase());
        batchMap.put("rule", DomainEventTypeEnum.SORT.name().toLowerCase());
    }


    @Override
    public List<String> pullLastEventMsgsFromRemoteCache(){

        try {
            Date lastDate = DateUtil.getLastMinute(LAST_MINUTES);
            //从数据中查询近几分钟的领域事件
            List<DomainEventDO> domainEventDOList= domainEventDAO.queryByGmtModify(lastDate);
            if(domainEventDOList.isEmpty()){
                return new ArrayList<>();
            }

            //业务去重
            return deduplicationd(domainEventDOList);

        } catch (Exception e) {
            logger.error("update rule form redis error!",e);
        }
        return new ArrayList<>();
    }


    /**
     * 按业务去重,只保留一个对象的uuid最新时间的变更,
     * 除batchMap里面的实体，其他原先一个消息里面data有多条记录变成一个记录一条消息
     */
    private List<String> deduplicationd(List<DomainEventDO> domainEventDOList){

        if(domainEventDOList == null || domainEventDOList.isEmpty()){
            return new ArrayList<>();
        }

        Map<String,DomainEventDO> target = new LinkedHashMap<>();

        for(DomainEventDO domainEventDO :domainEventDOList) {
            String eventDataStr = domainEventDO.getEventData();
            List<HashMap> jsonArray = JSON.parseArray(eventDataStr,HashMap.class);
            if(jsonArray == null || jsonArray.isEmpty()){
                continue;
            }

            String eventUuid = domainEventDO.getUuid();
            String eventType = domainEventDO.getEventType();
            String entity = domainEventDO.getEntity();
            Date occurredTime = domainEventDO.getGmtModify();


            List<String> batchEventList =  batchMap.get(entity);
            boolean isBatchEvent = false;
            //不做拆分的批量动作
            for(String batchEvent:batchEventList){
                if(eventType.endsWith(batchEvent)){
                    String uuid = JsonUtil.getString(((Map)jsonArray.get(0)),"uuid");
                    target.put(uuid+"_batch", domainEventDO);
                    isBatchEvent = true;
                    break;
                }
            }
            if(isBatchEvent){
                continue;
            }


            for(Map data :jsonArray) {
                String uuid = JsonUtil.getString(data,"uuid");
                DomainEventDO oldDomainEventDO = target.get(uuid);
                if(oldDomainEventDO == null || oldDomainEventDO.getGmtModify().getTime()<occurredTime.getTime()){
                    DomainEventDO newDomainEventDO = new DomainEventDO();
                    newDomainEventDO.setUuid(eventUuid);
                    newDomainEventDO.setEventType(eventType);
                    newDomainEventDO.setEntity(entity);
                    newDomainEventDO.setGmtModify(occurredTime);
                    List targetDatas = new ArrayList();
                    targetDatas.add(data);
                    newDomainEventDO.setEventData(JSON.toJSONString(targetDatas));

                    target.put(uuid, newDomainEventDO);
                }
            }
        }

        return target.values().stream().map(domainEventDO ->{
            Map event =new HashMap();
            event.put("uuid",domainEventDO.getUuid());
            event.put("eventType",domainEventDO.getEventType());
            event.put("occurredTime",domainEventDO.getGmtModify());
            event.put("entity",domainEventDO.getEntity());
            event.put("data",JSON.parseArray(domainEventDO.getEventData(),HashMap.class));
            return JSON.toJSONString(event);
        }).collect(Collectors.toList());
    }


    @Override
    public void putEventMsgToRemoteCache(String eventMsg,Long occurredTime){
        throw new RuntimeException("putEventMsgToRemoteCache not support");
    }
}
