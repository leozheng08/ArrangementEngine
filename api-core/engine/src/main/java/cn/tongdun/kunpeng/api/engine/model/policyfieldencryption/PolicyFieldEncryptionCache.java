package cn.tongdun.kunpeng.api.engine.model.policyfieldencryption;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hls
 * @version 1.0
 * @date 2021/11/1 7:29 下午
 */
@Component
public class PolicyFieldEncryptionCache extends AbstractLocalCache<String, List<PolicyFieldEncryption>> {

    //key:policyUuid  value:List<PolicyFieldEncryption>
    private Map<String, List<PolicyFieldEncryption>> fieldEncryptionMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        register(PolicyFieldEncryption.class);
    }

    @Override
    public List<PolicyFieldEncryption> get(String key) {
        return fieldEncryptionMap.get(key);
    }

    @Override
    public void put(String key, List<PolicyFieldEncryption> value) {
        fieldEncryptionMap.put(key, value);
    }

    @Override
    public List<PolicyFieldEncryption> remove(String uuid) {
        return fieldEncryptionMap.remove(uuid);
    }
}
