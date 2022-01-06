package cn.tongdun.kunpeng.api.engine.model.policyfieldnecessary;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.engine.model.policyfieldencryption.PolicyFieldEncryption;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hls
 * @version 1.0
 * @date 2021/11/1 7:33 下午
 */
@Component
public class PolicyFieldNecessaryCache extends AbstractLocalCache<String, List<PolicyFieldNecessary>> {
    //key:policyUuid  value:List<PolicyFieldEncryption>
    private Map<String, List<PolicyFieldNecessary>> fieldNecessaryMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        register(PolicyFieldNecessary.class);
    }

    @Override
    public List<PolicyFieldNecessary> get(String key) {
        return fieldNecessaryMap.get(key);
    }

    @Override
    public void put(String key, List<PolicyFieldNecessary> value) {
        fieldNecessaryMap.put(key, value);
    }

    @Override
    public List<PolicyFieldNecessary> remove(String uuid) {
        return fieldNecessaryMap.remove(uuid);
    }
}
