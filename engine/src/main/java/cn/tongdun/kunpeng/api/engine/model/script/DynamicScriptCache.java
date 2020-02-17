package cn.tongdun.kunpeng.api.engine.model.script;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 子策略缓存subPolicyUuid -> SubPolicy
 * @Author: liang.chen
 * @Date: 2019/12/16 下午8:01
 */
@Component
public class DynamicScriptCache extends AbstractLocalCache<String,DynamicScript> {

    //subPolicyUuid -> SubPolicy
    private Map<String,DynamicScript> subPolicyMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        register(DynamicScript.class);
    }

    @Override
    public DynamicScript get(String uuid){
        return subPolicyMap.get(uuid);
    }

    @Override
    public void put(String uuid, DynamicScript subPolicy){
        subPolicyMap.put(uuid,subPolicy);
    }

    @Override
    public DynamicScript remove(String uuid){
        return subPolicyMap.remove(uuid);
    }
}
