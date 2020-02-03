package cn.tongdun.kunpeng.api.core.field;

import cn.tongdun.kunpeng.api.core.eventtype.EventType;
import cn.tongdun.kunpeng.api.core.eventtype.IEventTypeRepository;
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
     */
    private Map<String, List<FieldDefinition>> systemFieldMap = new ConcurrentHashMap<>();

    /**
     * 系统字段
     */
    private Map<String, List<FieldDefinition>> extendFieldMap = new ConcurrentHashMap<>();



    public void addSystemField(FieldDefinition field, List<EventType> eventTypeList) {
        if (field == null) {
            return;
        }
        String eventType = field.getEventType();
        String appTypeStr = field.getAppType();
        if(StringUtils.isBlank(appTypeStr)){
            return;
        }

        String[] appTypeArr = appTypeStr.split(",");
        // 如果为null，是通用字段，放到所有事件类型列表中
        if (eventType == null || IEventTypeRepository.EVENT_TYPE_ALL.equalsIgnoreCase(eventType)) {
            for (EventType et : eventTypeList) {
                for (String appType : appTypeArr) {
                    List<FieldDefinition> list = systemFieldMap.get(et.getName() + "." + appType);
                    if (list == null) {
                        list = new ArrayList<FieldDefinition>();
                    }
                    list.add(field);
                    systemFieldMap.put(et.getName() + "." + appType, list);
                }
            }
        } else {
            for (String appType : appTypeArr) {
                String key = getSystemFieldKey(eventType, appType);
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
    }

    public void addExtendField(FieldDefinition field, List<EventType> eventTypeList) {
        if (field == null) {
            return;
        }

        // 字段类型为All，为同一个partnerCode和appName共用
        if (IEventTypeRepository.EVENT_TYPE_ALL.equals(field.getEventType())) {
            for (EventType et : eventTypeList) {
                String key = getExtendFieldKey(field.getPartnerCode(), field.getAppName(), et.getName());
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
            String key = getExtendFieldKey(field.getPartnerCode(), field.getAppName(), field.getEventType());
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



    public static String getExtendFieldKey(String partnerCode, String appName, String eventType) {
        if (StringUtils.isNotBlank(partnerCode) && StringUtils.isNotBlank(appName) && StringUtils.isNotBlank(eventType)) {
            return partnerCode + "." + appName + "." + eventType;
        }
        return null;
    }

    public static String getSystemFieldKey(String eventType, String appType) {
        if (StringUtils.isNotBlank(eventType) && StringUtils.isNotBlank(appType)) {
            return eventType + "." + appType;
        }
        return null;
    }


}
