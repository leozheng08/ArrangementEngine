package cn.tongdun.kunpeng.api.engine.model.field;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventTypeCache;
import cn.tongdun.kunpeng.api.engine.model.eventtype.IEventTypeRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
     * eventType -> List<FieldDefinition>>
     */
    private Map<String, List<FieldDefinition>> systemFieldMap = new ConcurrentHashMap<>(200);

    /**
     * 扩展字段
     * partnerCode.eventType -> List<FieldDefinition>>
     */
    private Map<String, List<FieldDefinition>> extendFieldMap = new ConcurrentHashMap<>(200);


    @Autowired
    private EventTypeCache eventTypeCacheRepository;

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


    public List<FieldDefinition> getSystemField(AbstractFraudContext context){
        return getSystemField(context.getEventType());
    }

    public List<FieldDefinition> getSystemField(String eventType){
        String key = getSystemFieldKey(eventType);

        return systemFieldMap.get(key);
    }

    public List<FieldDefinition> getExtendField(AbstractFraudContext context){
        return getExtendField(context.getPartnerCode(),context.getEventType());
    }

    public List<FieldDefinition> getExtendField(String partnerCode, String eventType){
        String key = getExtendFieldKey(partnerCode,eventType);

        return extendFieldMap.get(key);
    }


    public void addSystemField(FieldDefinition field) {
        if (field == null) {
            return;
        }
        String eventType = field.getEventType();
        Collection<EventType> eventTypeList=eventTypeCacheRepository.getEventTypes();

        // 如果为null，是通用字段，放到所有事件类型列表中
        if (eventType == null || IEventTypeRepository.EVENT_TYPE_ALL.equalsIgnoreCase(eventType)) {
            for (EventType et : eventTypeList) {
                String key = getSystemFieldKey(et.getEventCode());
                List<FieldDefinition> list = systemFieldMap.get(key);
                if (list == null) {
                    list = new ArrayList<FieldDefinition>();
                }
                list.add(field);
                systemFieldMap.put(key, list);
            }
        } else {
            String key = getSystemFieldKey(eventType);
            if (StringUtils.isBlank(key)) {
                return;
            }
            List<FieldDefinition> list = systemFieldMap.get(key);
            if (list == null) {
                list = new ArrayList<FieldDefinition>();
            }
            list.add(field);
            systemFieldMap.put(key, list);
        }
    }

    public void addExtendField(FieldDefinition field) {
        if (field == null) {
            return;
        }
        Collection<EventType> eventTypeList=eventTypeCacheRepository.getEventTypes();
        // 字段类型为All，为同一个partnerCode共用
        if (IEventTypeRepository.EVENT_TYPE_ALL.equalsIgnoreCase(field.getEventType())) {
            for (EventType et : eventTypeList) {
                String key = getExtendFieldKey(field.getPartnerCode(), et.getEventCode());
                if (StringUtils.isBlank(key)) {
                    return;
                }
                List<FieldDefinition> list = extendFieldMap.get(key);
                if (list == null) {
                    list = new ArrayList<FieldDefinition>();
                }
                list.add(field);
                extendFieldMap.put(key, list);
            }
        } else {
            String key = getExtendFieldKey(field.getPartnerCode(), field.getEventType());
            if (StringUtils.isBlank(key)) {
                return;
            }
            List<FieldDefinition> list = extendFieldMap.get(key);
            if (list == null) {
                list = new ArrayList<FieldDefinition>();
            }
            list.add(field);
            extendFieldMap.put(key, list);
        }
    }

    /**
     * 删除systemFieldMap或extendFieldMap中的字段定义
     * @param uuid
     */
    public void removeField(String uuid, Map<String, List<FieldDefinition>> fieldMap){

        fieldMap.values().forEach(new Consumer<List<FieldDefinition>>() {
            @Override
            public void accept(List<FieldDefinition> fieldDefinitions) {
                fieldDefinitions.removeIf(new Predicate<FieldDefinition>() {
                    @Override
                    public boolean test(FieldDefinition fieldDefinition) {
                        if(fieldDefinition.getUuid().equals(uuid)) {
                            return true;
                        }
                        return false;
                    }
                });
            }
        });

    }

    /**
     * todo 修改时，删除systemFieldMap或extendFieldMap中的多余数据
     * @param
     */
    public void replaceField(FieldDefinition oldFieldDefinition, Map<String, List<FieldDefinition>> fieldMap){

        fieldMap.values().forEach(new Consumer<List<FieldDefinition>>() {
            @Override
            public void accept(List<FieldDefinition> fieldDefinitions) {
                fieldDefinitions.removeIf(new Predicate<FieldDefinition>() {
                    @Override
                    public boolean test(FieldDefinition fieldDefinition) {
                        if(fieldDefinition.getUuid().equals(oldFieldDefinition.getUuid())) {

                            return true;
                        }
                        return false;
                    }
                });
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
