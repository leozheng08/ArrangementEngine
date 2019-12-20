package cn.tongdun.kunpeng.api.policy;

import cn.tongdun.kunpeng.api.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.cache.ILocalCache;
import cn.tongdun.kunpeng.api.subpolicy.SubPolicy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 策略缓存policyUuid -> Policy
 * @Author: liang.chen
 * @Date: 2019/12/16 下午8:01
 */
@Component
public class PolicyCache extends AbstractLocalCache<String,Policy> {

    //policyUuid -> Policy
    private Map<String,Policy> policyMap = new ConcurrentHashMap<>(5000);

    @PostConstruct
    public void init(){
        register(Policy.class);
    }

    @Override
    public Policy get(String uuid){
        return policyMap.get(uuid);
    }

    @Override
    public void put(String uuid, Policy policy){
        policyMap.put(uuid,policy);
    }

    @Override
    public void remove(String uuid){
        policyMap.remove(uuid);
    }
}
