package cn.tongdun.kunpeng.api.subpolicy;

import cn.tongdun.kunpeng.api.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.cache.ILocalCache;
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
public class SubPolicyCache extends AbstractLocalCache<String,SubPolicy> {

    //subPolicyUuid -> SubPolicy
    private Map<String,SubPolicy> subPolicyMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        register(SubPolicy.class);
    }

    @Override
    public SubPolicy get(String uuid){
        return subPolicyMap.get(uuid);
    }

    @Override
    public void put(String uuid, SubPolicy subPolicy){
        subPolicyMap.put(uuid,subPolicy);
    }

    @Override
    public SubPolicy remove(String uuid){
        return subPolicyMap.remove(uuid);
    }
}
