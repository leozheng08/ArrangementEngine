package cn.tongdun.kunpeng.api.engine.model.Indicatrix;

import cn.tongdun.kunpeng.api.engine.cache.AbstractBatchLocalCache;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class PlatformIndexCache extends AbstractBatchLocalCache<String, String> {

    private ConcurrentMap<String, List<String>> policyIndicatrixItem = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        register(String.class);
    }

    @Override
    public List<String> getList(String policyUuid) {
        return policyIndicatrixItem.get(policyUuid);
    }

    @Override
    public void putList(String policyUuid, List<String> policyIndicatrixItemList) {
        policyIndicatrixItem.put(policyUuid, policyIndicatrixItemList);
    }

    @Override
    public List<String> removeList(String policyUuid) {
        return policyIndicatrixItem.remove(policyUuid);
    }
}
