package cn.tongdun.kunpeng.api.engine.model.field;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventTypeCache;
import cn.tongdun.kunpeng.api.engine.model.eventtype.IEventTypeRepository;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:45
 */
@Component
@Data
public class FieldDefinitionCache extends AbstractLocalCache<String,FieldDefinition> {

    //uuid -> Policy
    private Map<String,FieldDefinition> fieldDefinitionMap = new ConcurrentHashMap<>(500);

    /**
     * 系统字段
     * eventType -> uuid -> Policy
     */
    private Map<String, Map<String,FieldDefinition>> systemFieldMap = new ConcurrentHashMap<>(200);

    /**
     * 扩展字段
     * eventType -> uuid -> Policy
     */
    private Map<String, Map<String,FieldDefinition>> extendFieldMap = new ConcurrentHashMap<>(200);


    @Autowired
    private EventTypeCache eventTypeCache;

    @PostConstruct
    public void init(){
        register(FieldDefinition.class);
    }

    @Override
    public FieldDefinition get(String uuid){
        return fieldDefinitionMap.get(uuid);
    }

    @Override
    public void put(String uuid, FieldDefinition fieldDefinition){
        FieldDefinition oldfieldDefinition = fieldDefinitionMap.put(uuid,fieldDefinition);
        if("sys".equalsIgnoreCase(fieldDefinition.getFieldType())){
            addSystemField(fieldDefinition);
        } else {
            addExtendField(fieldDefinition);
        }

        if(oldfieldDefinition != null){

        }
    }

    @Override
    public FieldDefinition remove(String uuid){
        FieldDefinition fieldDefinition = fieldDefinitionMap.remove(uuid);
        if(fieldDefinition == null){
            return null;
        }

        if("sys".equalsIgnoreCase(fieldDefinition.getFieldType())){
            removeField(uuid,systemFieldMap);
        } else {
            removeField(uuid,extendFieldMap);
        }

        return fieldDefinition;
    }


    public Collection<FieldDefinition> getSystemField(AbstractFraudContext context){
        return getSystemField(context.getEventType());
    }

    public Collection<FieldDefinition> getSystemField(String eventType){
        String key = getSystemFieldKey(eventType);
        Map<String,FieldDefinition> sysFieldMap = systemFieldMap.get(key);
        if(sysFieldMap == null){
            return null;
        }
        return sysFieldMap.values();
    }

    public Collection<FieldDefinition> getExtendField(AbstractFraudContext context){
        return getExtendField(context.getPartnerCode(),context.getEventType());
    }

    public Collection<FieldDefinition> getExtendField(String partnerCode, String eventType){
        String key = getExtendFieldKey(partnerCode,eventType);
        Map<String,FieldDefinition> sysFieldMap = extendFieldMap.get(key);
        if(sysFieldMap == null){
            return null;
        }
        return sysFieldMap.values();
    }



    private Map<String,FieldDefinition> getFieldMap(Map<String, Map<String,FieldDefinition>> fieldMap,String key){
        Map<String,FieldDefinition> map = fieldMap.get(key);
        if (map == null) {
            map = new HashMap<>();
            Map<String,FieldDefinition> oldMap = fieldMap.putIfAbsent(key, map);
            if(oldMap != null){
                map = oldMap;
            }
        }
        return map;
    }

    public void addSystemField(FieldDefinition field) {
        if (field == null) {
            return;
        }
        String eventType = field.getEventType();
        Collection<EventType> eventTypeList= eventTypeCache.getEventTypes();

        // 如果为null，是通用字段，放到所有事件类型列表中
        if (StringUtils.isBlank(eventType) || IEventTypeRepository.EVENT_TYPE_ALL.equalsIgnoreCase(eventType)) {
            for (EventType et : eventTypeList) {
                String key = getSystemFieldKey(et.getEventCode());
                Map<String,FieldDefinition> map = getFieldMap(systemFieldMap,key);
                map.put(field.getUuid(),field);
            }
        } else {
            String key = getSystemFieldKey(eventType);
            if (StringUtils.isBlank(key)) {
                return;
            }
            Map<String,FieldDefinition> map = getFieldMap(systemFieldMap,key);
            map.put(field.getUuid(),field);

            //删除其他事件类型下的此字段
            for (EventType et : eventTypeList) {
                String sysKey = getSystemFieldKey(et.getEventCode());
                if(key.equals(sysKey)){
                    continue;
                }
                Map<String,FieldDefinition> sysMap = getFieldMap(systemFieldMap,sysKey);
                sysMap.remove(field.getUuid());
            }
        }
    }

    public void addExtendField(FieldDefinition field) {
        if (field == null) {
            return;
        }
        Collection<EventType> eventTypeList= eventTypeCache.getEventTypes();
        // 字段类型为All，为同一个partnerCode共用
        if (IEventTypeRepository.EVENT_TYPE_ALL.equalsIgnoreCase(field.getEventType())) {
            for (EventType et : eventTypeList) {
                String key = getExtendFieldKey(field.getPartnerCode(), et.getEventCode());
                if (StringUtils.isBlank(key)) {
                    return;
                }
                Map<String,FieldDefinition> map = getFieldMap(extendFieldMap,key);
                map.put(field.getUuid(),field);
            }
        } else {
            String key = getExtendFieldKey(field.getPartnerCode(), field.getEventType());
            if (StringUtils.isBlank(key)) {
                return;
            }
            Map<String,FieldDefinition> map = getFieldMap(extendFieldMap,key);
            map.put(field.getUuid(),field);

            //删除其他事件类型下的此字段
            for (EventType et : eventTypeList) {
                String extKey = getExtendFieldKey(field.getPartnerCode(), et.getEventCode());
                if(key.equals(extKey)){
                    continue;
                }
                Map<String,FieldDefinition> sysMap = getFieldMap(extendFieldMap,extKey);
                sysMap.remove(field.getUuid());
            }
        }
    }

    /**
     * 删除systemFieldMap或extendFieldMap中的字段定义
     * @param uuid
     */
    public void removeField(String uuid, Map<String, Map<String,FieldDefinition>> fieldMap){

        fieldMap.values().forEach(new Consumer<Map<String,FieldDefinition>>() {
            @Override
            public void accept(Map<String,FieldDefinition> map) {
                map.remove(uuid);
            }
        });

    }


    private String getExtendFieldKey(String partnerCode, String eventType) {
        if (StringUtils.isNotBlank(partnerCode) && StringUtils.isNotBlank(eventType)) {
            return StringUtils.join(partnerCode , "." , eventType);
        }
        return null;
    }

    private String getSystemFieldKey(String eventType) {
        return eventType;
    }


}
