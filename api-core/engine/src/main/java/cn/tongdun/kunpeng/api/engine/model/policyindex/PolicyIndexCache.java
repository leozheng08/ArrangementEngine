package cn.tongdun.kunpeng.api.engine.model.policyindex;

import cn.tongdun.kunpeng.api.engine.cache.AbstractBatchLocalCache;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.AbstractDecisionMode;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: liuq
 * @Date: 2020/2/18 10:49 AM
 */
@Component
public class PolicyIndexCache extends AbstractBatchLocalCache<String, PolicyIndex> {

    private ConcurrentMap<String, List<PolicyIndex>> policyUuid2PolicyIndex = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        register(PolicyIndex.class);
    }

    @Override
    public List<PolicyIndex> getList(String policyUuid) {
        return policyUuid2PolicyIndex.get(policyUuid);
    }

    @Override
    public void putList(String policyUuid, List<PolicyIndex> policyIndexList) {
        policyUuid2PolicyIndex.put(policyUuid, policyIndexList);
    }

    @Override
    public List<PolicyIndex> removeList(String policyUuid) {
        return policyUuid2PolicyIndex.remove(policyUuid);
    }
}
