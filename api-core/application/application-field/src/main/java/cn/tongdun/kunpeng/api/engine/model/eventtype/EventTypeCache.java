package cn.tongdun.kunpeng.api.engine.model.eventtype;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:45
 */
@Component
@Data
public class EventTypeCache  extends AbstractLocalCache<String,EventType> {

    private Map<String,EventType> eventTypeMap =  new ConcurrentHashMap<>(20);


    @PostConstruct
    public void init(){
        register(EventType.class);
    }

    @Override
    public EventType get(String uuid){
        return eventTypeMap.get(uuid);
    }

    @Override
    public void put(String uuid, EventType policy){
        eventTypeMap.put(uuid,policy);
    }

    @Override
    public EventType remove(String uuid){
        return eventTypeMap.remove(uuid);
    }

    public Collection<EventType> getEventTypes(){
        return eventTypeMap.values();
    }

}
