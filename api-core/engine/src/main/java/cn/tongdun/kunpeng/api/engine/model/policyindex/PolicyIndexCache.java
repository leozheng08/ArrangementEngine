package cn.tongdun.kunpeng.api.engine.model.policyindex;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: liuq
 * @Date: 2020/2/18 10:49 AM
 */
@Component
public class PolicyIndexCache extends AbstractLocalCache<String, Map<String, PolicyIndex>> {
    // <policyUuid,<policyIndexUuid,PolicyIndex>>
    private ConcurrentMap<String, Map<String,PolicyIndex>> policyUuid2PolicyIndexMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        register(PolicyIndex.class);
    }

    @Override
    public Map<String, PolicyIndex> get(String policyUuid) {
        return policyUuid2PolicyIndexMap.get(policyUuid);
    }

    @Override
    public void put(String policyUuid, Map<String, PolicyIndex> policyIndexMap) {
        policyUuid2PolicyIndexMap.put(policyUuid, policyIndexMap);
    }

    @Override
    public Map<String, PolicyIndex> remove(String policyUuid) {
        return policyUuid2PolicyIndexMap.remove(policyUuid);
    }
}
