package cn.tongdun.kunpeng.api.policy;

import cn.tongdun.kunpeng.api.subpolicy.SubPolicy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 策略缓存policyUuid -> Policy
 * @Author: liang.chen
 * @Date: 2019/12/16 下午8:01
 */
@Component
public class PolicyCache {

    //policyUuid -> Policy
    private Map<String,Policy> policyMap = new ConcurrentHashMap<>(5000);

    public Policy getPolicy(String uuid){
        return policyMap.get(uuid);
    }

    public void putPolicy(String uuid,Policy policy){
        policyMap.put(uuid,policy);
    }

    public void removePolicy(String uuid){
        policyMap.remove(uuid);
    }
}
