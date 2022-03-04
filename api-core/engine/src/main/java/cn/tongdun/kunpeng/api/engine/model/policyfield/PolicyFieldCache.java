package cn.tongdun.kunpeng.api.engine.model.policyfield;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.engine.dto.PolicyFieldDTO;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zeyuan.zheng@tongdun.cn
 * @date 2/16/22 8:19 PM
 * 策略字段缓存，区别于策略加密字段，策略必选字段
 */
@Component
public class PolicyFieldCache extends AbstractLocalCache<String, List<PolicyField>> {

    Map<String, List<PolicyField>> policyFieldItemMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        register(PolicyFieldCache.class);
    }

    @Override
    public List<PolicyField> get(String key) {
        return policyFieldItemMap.get(key);
    }

    @Override
    public void put(String key, List<PolicyField> value) {
        policyFieldItemMap.put(key, value);
    }

    @Override
    public List<PolicyField> remove(String uuid) {
        return policyFieldItemMap.remove(uuid);
    }
}
