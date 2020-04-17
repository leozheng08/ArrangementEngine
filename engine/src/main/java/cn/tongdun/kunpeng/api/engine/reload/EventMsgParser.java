package cn.tongdun.kunpeng.api.engine.reload;

import cn.tongdun.kunpeng.api.engine.reload.dataobject.*;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/3/6 下午12:29
 */
@Component
public class EventMsgParser {

    private Logger logger = LoggerFactory.getLogger(EventMsgParser.class);

    private Map<String,Class> entityMap = new HashMap<String,Class>(){{
        put("policy_definition", PolicyDefinitionEventDO.class);
        put("policy", PolicyEventDO.class);
        put("sub_policy", SubPolicyEventDO.class);
        put("rule", RuleEventDO.class);
        put("decision_flow", DecisionFlowEventDO.class);
        put("index_definition", IndexDefinitionEventDO.class);
        put("index", IndexDefinitionEventDO.class);
        put("policy_decision_mode", PolicyDecisionModeEventDO.class);
        put("field_definition", FieldDefinitionEventDO.class);
        put("field", FieldDefinitionEventDO.class);
        put("dynamic_script", DynamicScriptEventDO.class);
        put("interface_definition",InterfaceDefinitionEventDO.class);
        put("policy_challenger",PolicyChallengerEventDO.class);
        put("event_type",EventTypeEventDO.class);
        put("partner",PartnerEventDO.class);

        put("custom_list_value",CustomListValueEventDO.class);
    }};


    public DomainEvent parse(String event){
        event = event.replaceAll("DISABLED","0");
        event = event.replaceAll("ENABLED","1");
        Map map = JSON.parseObject(event,HashMap.class);
        return parse(map);
    }

    public DomainEvent parse(Map jsonObject){

        String entity = JsonUtil.getString(jsonObject,"entity");
        DomainEvent domainEvent = new DomainEvent();
        domainEvent.setOccurredTime(JsonUtil.getLong(jsonObject,"occurredTime"));
        domainEvent.setEntity(entity);
        domainEvent.setEventType(JsonUtil.getString(jsonObject,"eventType"));
        List<Map> jsonArray = (List<Map>)jsonObject.get("data");

        if(entity == null){
            domainEvent.setData(jsonArray);
            domainEvent.setEntityClass(Map.class);
//            logger.debug("event entity is empty! event:{}",jsonObject.toString());
            return domainEvent;
        }

        Class entityClass = entityMap.get(entity.toLowerCase());
        if(entityClass == null){
            domainEvent.setData(jsonArray);
            domainEvent.setEntityClass(Map.class);
//            logger.debug("event entityClass not find! event:{}",jsonObject.toString());
            return domainEvent;
        }
        domainEvent.setEntityClass(entityClass);
        List entityList = new ArrayList();
        if(jsonArray != null){
            for(Map data:jsonArray){
                Long gmtModify = JsonUtil.getLong(data,"gmtModify");
                if(gmtModify == null || gmtModify< domainEvent.getOccurredTime()) {
                    data.put("gmtModify", domainEvent.getOccurredTime());
                }
                Object entityInst = JSON.parseObject(JSON.toJSONString(data),entityClass);
                entityList.add(entityInst);

            }
        }
        domainEvent.setData(entityList);

        return domainEvent;
    }
}
