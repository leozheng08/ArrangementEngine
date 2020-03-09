package cn.tongdun.kunpeng.api.engine.reload.reload;

import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.share.dataobject.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

    private Map<String,Class> entityMap = new HashMap<String,Class>(){{
        put("PolicyDefinition", PolicyDefinitionDO.class);
        put("Policy", PolicyDO.class);
        put("SubPolicy", SubPolicy.class);
        put("Rule", RuleDO.class);
        put("RuleConditionElement", RuleConditionElementDO.class);
        put("RuleActionElement", RuleActionElementDO.class);
        put("DecisionFlow", DecisionFlowDO.class);
        put("IndexDefinition", IndexDefinitionDO.class);
        put("PolicyDecisionMode", PolicyDecisionModeDO.class);
        put("FieldDefinition", FieldDefinitionDO.class);
        put("EventType", EventTypeDO.class);
        put("DynamicScript", DynamicScriptDO.class);
        put("CustomListValue", CustomListValueDO.class);
    }};

    public DomainEvent parse(String event){
        JSONObject jsonObject = JSONObject.parseObject(event);

        String entity = jsonObject.getString("entity");
        if(entity == null){
            throw new RuntimeException("event entity is empty!");
        }

        Class entityClass = entityMap.get(entity);
        if(entityClass == null){
            throw new RuntimeException("event entityClass not find!");
        }


        DomainEvent domainEvent = new DomainEvent();
        domainEvent.setOccurredTime(jsonObject.getLong("occurredTime"));
        domainEvent.setEntity(entity);
        domainEvent.setEventType(jsonObject.getString("eventType("));
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        List entityList = new ArrayList();
        if(jsonArray != null){
            for(Object obj:jsonArray){
                Object entityInst = ((JSONObject)obj).toJavaObject(entityClass);
                entityList.add(entityInst);
            }
        }
        domainEvent.setData(entityList);

        return domainEvent;
    }
}
