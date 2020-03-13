package cn.tongdun.kunpeng.api.engine.reload;

import cn.tongdun.kunpeng.share.dataobject.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
        put("policy_definition", PolicyDefinitionDO.class);
        put("policy", PolicyDO.class);
        put("sub_policy", SubPolicyDO.class);
        put("rule", RuleDO.class);
//        put("rule_condition_element", RuleConditionElementDO.class);
//        put("rule_action_element", RuleActionElementDO.class);
        put("decision_flow", DecisionFlowDO.class);
        put("index_definition", IndexDefinitionDO.class);
        put("policy_decision_mode", PolicyDecisionModeDO.class);
        put("field_definition", FieldDefinitionDO.class);
        put("event_type", EventTypeDO.class);
        put("dynamic_script", DynamicScriptDO.class);
        put("custom_list_value", CustomListValueDO.class);
        put("interface_definition",InterfaceDefinitionDO.class);
        put("policy_challenger",PolicyChallengerDO.class);
        put("eventtype",EventTypeDO.class);
        put("event_type",EventTypeDO.class);
    }};


    public DomainEvent parse(String event){
        event = event.replaceAll("DISABLED","0");
        event = event.replaceAll("ENABLED","1");
        JSONObject jsonObject = JSONObject.parseObject(event);
        return parse(jsonObject);
    }

    public DomainEvent parse(JSONObject jsonObject){

        String entity = jsonObject.getString("entity");
        DomainEvent domainEvent = new DomainEvent();
        domainEvent.setOccurredTime(jsonObject.getLong("occurredTime"));
        domainEvent.setEntity(entity);
        domainEvent.setEventType(jsonObject.getString("eventType"));
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        if(entity == null){
            domainEvent.setData(jsonArray);
            logger.warn("event entity is empty! event:{}",jsonObject.toString());
            return domainEvent;
        }

        Class entityClass = entityMap.get(entity.toLowerCase());
        if(entityClass == null){
            domainEvent.setData(jsonArray);
            logger.warn("event entityClass not find! event:{}",jsonObject.toString());
            return domainEvent;
        }

        List entityList = new ArrayList();
        if(jsonArray != null){
            for(Object obj:jsonArray){
                JSONObject data = (JSONObject)obj;
                Long gmtModify = data.getLong("gmtModify");
                if(gmtModify == null || gmtModify< domainEvent.getOccurredTime()) {
                    data.put("gmtModify", domainEvent.getOccurredTime());
                }
                Object entityInst = data.toJavaObject(entityClass);
                entityList.add(entityInst);
            }
        }
        domainEvent.setData(entityList);

        return domainEvent;
    }



    public SingleDomainEvent parseSingleDomainEvent(String event){
        event = event.replaceAll("DISABLED","0");
        event = event.replaceAll("ENABLED","1");

        JSONObject jsonObject = JSONObject.parseObject(event);
        String entity = jsonObject.getString("entity");
        SingleDomainEvent domainEvent = new SingleDomainEvent();
        domainEvent.setOccurredTime(jsonObject.getLong("occurredTime"));
        domainEvent.setEntity(entity);
        domainEvent.setEventType(jsonObject.getString("eventType"));
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        if(entity == null){
            domainEvent.setData(jsonArray);
            logger.warn("event entity is empty! event:{}",event);
            return domainEvent;
        }

        Class entityClass = entityMap.get(entity.toLowerCase());
        if(entityClass == null){
            domainEvent.setData(jsonArray);
            logger.warn("event entityClass not find! event:{}",event);
            return domainEvent;
        }


        if(jsonArray != null && !jsonArray.isEmpty()){
            JSONObject data = (JSONObject)jsonArray.get(0);
            Long gmtModify = data.getLong("gmtModify");
            if(gmtModify == null || gmtModify< domainEvent.getOccurredTime()) {
                data.put("gmtModify", domainEvent.getOccurredTime());
            }
            Object entityInst = data.toJavaObject(entityClass);
            domainEvent.setData(entityInst);
        }
        return domainEvent;
    }
}
