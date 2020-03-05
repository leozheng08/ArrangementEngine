package cn.tongdun.kunpeng.api.engine.model.field;

import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;
import cn.tongdun.kunpeng.api.engine.model.eventtype.IEventTypeRepository;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:45
 */
@Component
@Data
public class FieldDefinitionCache {

    /**
     * 系统字段
     * eventType -> List<FieldDefinition>>
     */
    private Map<String, List<FieldDefinition>> systemFieldMap = new ConcurrentHashMap<>(200);

    /**
     * 系统字段
     * partnerCode.eventType -> List<FieldDefinition>>
     */
    private Map<String, List<FieldDefinition>> extendFieldMap = new ConcurrentHashMap<>(200);


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


    public void addSystemField(FieldDefinition field, List<EventType> eventTypeList) {
        if (field == null) {
            return;
        }
        String eventType = field.getEventType();

        // 如果为null，是通用字段，放到所有事件类型列表中
        if (eventType == null || IEventTypeRepository.EVENT_TYPE_ALL.equalsIgnoreCase(eventType)) {
            for (EventType et : eventTypeList) {
                String key = getSystemFieldKey(et.getCode());
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

    public void addExtendField(FieldDefinition field, List<EventType> eventTypeList) {
        if (field == null) {
            return;
        }

        // 字段类型为All，为同一个partnerCode共用
        if (IEventTypeRepository.EVENT_TYPE_ALL.equals(field.getEventType())) {
            for (EventType et : eventTypeList) {
                String key = getExtendFieldKey(field.getPartnerCode(), et.getCode());
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
