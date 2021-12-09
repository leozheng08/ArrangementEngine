package cn.tongdun.kunpeng.api.engine.model.customoutput;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mengtao
 * @version 1.0
 * @date 2021/9/16 16:47
 */
@Component
public class PolicyCustomOutputCache extends AbstractLocalCache<String,List<PolicyCustomOutput>> {

    //key:policyUuid  value:List<policyCustomOutput>
    private Map<String, List<PolicyCustomOutput>> outputMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        register(PolicyCustomOutput.class);
    }

    @Override
    public List<PolicyCustomOutput> get(String key) {
        return outputMap.get(key);
    }

    @Override
    public void put(String key, List<PolicyCustomOutput> value) {
        outputMap.put(key,value);
    }

    @Override
    public List<PolicyCustomOutput> remove(String uuid) {
        return outputMap.remove(uuid);
    }
}
