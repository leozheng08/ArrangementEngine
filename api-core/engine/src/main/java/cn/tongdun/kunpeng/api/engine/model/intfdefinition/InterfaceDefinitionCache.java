package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 三方接口定义缓存interfaceDefinitionUuid -> Interface_definition
 * @Author: liang.chen
 * @Date: 2019/12/16 下午8:01
 */
@Component
public class InterfaceDefinitionCache extends AbstractLocalCache<String,InterfaceDefinition> {

    //interfaceDefinitionUuid -> InterfaceDefinition
    private Map<String,InterfaceDefinition> interfaceDefinitionMap = new ConcurrentHashMap<>(50);


    @PostConstruct
    public void init(){
        register(InterfaceDefinition.class);
    }

    @Override
    public InterfaceDefinition get(String uuid){
        return interfaceDefinitionMap.get(uuid);
    }

    @Override
    public void put(String uuid, InterfaceDefinition interfaceDefinition){
        interfaceDefinitionMap.put(uuid,interfaceDefinition);
    }

    @Override
    public InterfaceDefinition remove(String uuid){
        return interfaceDefinitionMap.remove(uuid);
    }
}
