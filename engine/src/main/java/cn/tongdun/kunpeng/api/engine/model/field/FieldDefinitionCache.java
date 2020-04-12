package cn.tongdun.kunpeng.api.engine.model.field;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventTypeCache;
import cn.tongdun.kunpeng.api.engine.model.eventtype.IEventTypeRepository;
import cn.tongdun.kunpeng.api.common.data.IFieldDefinition;
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
public class FieldDefinitionCache extends AbstractLocalCache<String,IFieldDefinition> {

    //uuid -> Policy
    private Map<String,IFieldDefinition> fieldDefinitionMap = new ConcurrentHashMap<>(500);

    /**
     * 系统字段
     * eventType -> fieldCode -> Policy
     */
    private Map<String, Map<String,IFieldDefinition>> systemFieldMap = new ConcurrentHashMap<>(200);

    /**
     * 扩展字段
     * partnerCode.eventType -> fieldCode -> Policy
     */
    private Map<String, Map<String,IFieldDefinition>> extendFieldMap = new ConcurrentHashMap<>(200);


    @Autowired
    private EventTypeCache eventTypeCache;

    @PostConstruct
    public void init(){
        register(IFieldDefinition.class);
    }

    @Override
    public IFieldDefinition get(String uuid){
        return fieldDefinitionMap.get(uuid);
    }

    @Override
    public void put(String uuid, IFieldDefinition fieldDefinition){
        IFieldDefinition oldfieldDefinition = fieldDefinitionMap.put(uuid,fieldDefinition);
        if("sys".equalsIgnoreCase(fieldDefinition.getFieldType())){
            addSystemField(fieldDefinition);
        } else {
            addExtendField(fieldDefinition);
        }

        if(oldfieldDefinition != null){

        }
    }

    @Override
    public IFieldDefinition remove(String uuid){
        IFieldDefinition fieldDefinition = fieldDefinitionMap.remove(uuid);
        if(fieldDefinition == null){
            return null;
        }

        if("sys".equalsIgnoreCase(fieldDefinition.getFieldType())){
            removeField(fieldDefinition.getFieldCode(),systemFieldMap);
        } else {
            removeField(fieldDefinition.getFieldCode(),extendFieldMap);
        }

        return fieldDefinition;
    }


    public Map<String,IFieldDefinition> getSystemField(AbstractFraudContext context){
        return getSystemField(context.getEventType());
    }

    public Map<String,IFieldDefinition> getSystemField(String eventType){
        String key = getSystemFieldKey(eventType);
        Map<String,IFieldDefinition> sysFieldMap = systemFieldMap.get(key);
        return sysFieldMap;
    }

    public Map<String,IFieldDefinition> getExtendField(AbstractFraudContext context){
        return getExtendField(context.getPartnerCode(),context.getEventType());
    }

    public Map<String,IFieldDefinition> getExtendField(String partnerCode, String eventType){
        String key = getExtendFieldKey(partnerCode,eventType);
        Map<String,IFieldDefinition> sysFieldMap = extendFieldMap.get(key);
        return sysFieldMap;
    }



    private Map<String,IFieldDefinition> getFieldMap(Map<String, Map<String,IFieldDefinition>> fieldMap,String key){
        Map<String,IFieldDefinition> map = fieldMap.get(key);
        if (map == null) {
            map = new HashMap<>();
            Map<String,IFieldDefinition> oldMap = fieldMap.putIfAbsent(key, map);
            if(oldMap != null){
                map = oldMap;
            }
        }
        return map;
    }

    public void addSystemField(IFieldDefinition field) {
        if (field == null) {
            return;
        }
        String eventType = field.getEventType();
        Collection<EventType> eventTypeList= eventTypeCache.getEventTypes();

        // 如果为null，是通用字段，放到所有事件类型列表中
        if (StringUtils.isBlank(eventType) || IEventTypeRepository.EVENT_TYPE_ALL.equalsIgnoreCase(eventType)) {
            for (EventType et : eventTypeList) {
                String key = getSystemFieldKey(et.getEventCode());
                Map<String,IFieldDefinition> map = getFieldMap(systemFieldMap,key);
                map.put(field.getFieldCode(),field);
            }
        } else {
            String key = getSystemFieldKey(eventType);
            if (StringUtils.isBlank(key)) {
                return;
            }
            Map<String,IFieldDefinition> map = getFieldMap(systemFieldMap,key);
            map.put(field.getFieldCode(),field);

            //删除其他事件类型下的此字段
            for (EventType et : eventTypeList) {
                String sysKey = getSystemFieldKey(et.getEventCode());
                if(key.equals(sysKey)){
                    continue;
                }
                Map<String,IFieldDefinition> sysMap = getFieldMap(systemFieldMap,sysKey);
                sysMap.remove(field.getFieldCode());
            }
        }
    }

    public void addExtendField(IFieldDefinition field) {
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
                Map<String,IFieldDefinition> map = getFieldMap(extendFieldMap,key);
                map.put(field.getFieldCode(),field);
            }
        } else {
            String key = getExtendFieldKey(field.getPartnerCode(), field.getEventType());
            if (StringUtils.isBlank(key)) {
                return;
            }
            Map<String,IFieldDefinition> map = getFieldMap(extendFieldMap,key);
            map.put(field.getFieldCode(),field);

            //删除其他事件类型下的此字段
            for (EventType et : eventTypeList) {
                String extKey = getExtendFieldKey(field.getPartnerCode(), et.getEventCode());
                if(key.equals(extKey)){
                    continue;
                }
                Map<String,IFieldDefinition> sysMap = getFieldMap(extendFieldMap,extKey);
                sysMap.remove(field.getFieldCode());
            }
        }
    }

    /**
     * 删除systemFieldMap或extendFieldMap中的字段定义
     * @param fieldCode
     */
    public void removeField(String fieldCode, Map<String, Map<String,IFieldDefinition>> fieldMap){

        fieldMap.values().forEach(new Consumer<Map<String,IFieldDefinition>>() {
            @Override
            public void accept(Map<String,IFieldDefinition> map) {
                map.remove(fieldCode);
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
