package cn.tongdun.kunpeng.api.engine.reload.docache;

import cn.tongdun.kunpeng.share.dataobject.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: liang.chen
 * @Date: 2020/4/26 下午2:33
 */
@Component
public class DataObjectCacheFactory {
    //保存各个DO类对应的 缓存实现类
    Map<Class, IDataObjectCache> cacheMap = new ConcurrentHashMap<>(10);


    private static final Map<String,Class> ENTITY_MAP = new HashMap<String,Class>(){{
        put("policy_definition", PolicyDefinitionDO.class);
        put("policy", PolicyDO.class);
        put("sub_policy", SubPolicyDO.class);
        put("rule", RuleDO.class);
        put("decision_flow", DecisionFlowDO.class);
        put("index_definition", IndexDefinitionDO.class);
        put("index", IndexDefinitionDO.class);
        put("policy_decision_mode", PolicyDecisionModeDO.class);
        put("field_definition", FieldDefinitionDO.class);
        put("field", FieldDefinitionDO.class);
        put("dynamic_script", DynamicScriptDO.class);
        put("interface_definition",InterfaceDefinitionDO.class);
        put("policy_challenger",PolicyChallengerDO.class);
        put("event_type",EventTypeDO.class);
        put("custom_list_value",CustomListValueDO.class);
        put("policy_custom_output",PolicyCustomOutputDO.class);
    }};

    public void register(Class clazz, IDataObjectCache doCache) {
        cacheMap.put(clazz, doCache);
    }

    public IDataObjectCache getDOCache(Class valueClazz) {
        return cacheMap.get(valueClazz);
    }

    public IDataObjectCache getDOCacheByName(String name){
        Class clazz = ENTITY_MAP.get(name);
        if(clazz == null){
            return null;
        }
        return getDOCache(clazz);
    }

    public Collection<IDataObjectCache> getDOCaches() {
        return cacheMap.values();
    }
}
