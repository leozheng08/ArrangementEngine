package cn.tongdun.kunpeng.api.subpolicy;

import cn.tongdun.kunpeng.api.rule.Rule;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 子策略缓存subPolicyUuid -> SubPolicy
 * @Author: liang.chen
 * @Date: 2019/12/16 下午8:01
 */
@Component
public class SubPolicyCache {

    //subPolicyUuid -> SubPolicy
    private Map<String,SubPolicy> subPolicyMap = new ConcurrentHashMap<>();


    public SubPolicy getSubPolicy(String uuid){
        return subPolicyMap.get(uuid);
    }

    public void putSubPolicy(String uuid,SubPolicy subPolicy){
        subPolicyMap.put(uuid,subPolicy);
    }

    public void removeSubPolicy(String uuid){
        subPolicyMap.remove(uuid);
    }
}
