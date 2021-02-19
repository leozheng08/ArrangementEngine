package cn.tongdun.kunpeng.api.engine.model.subpolicy;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 子策略缓存subPolicyUuid -> SubPolicy
 * @Author: liang.chen
 * @Date: 2019/12/16 下午8:01
 */
@Component
public class SubPolicyCache extends AbstractLocalCache<String,SubPolicy> {

    //subPolicyUuid -> SubPolicy
    private Map<String,SubPolicy> subPolicyMap = new ConcurrentHashMap<>(1000);

    //policyUuid -> Set<subPolicyUuid>
    private Map<String,Set<String>>  policyUuidToSubPolicyMap = new ConcurrentHashMap<>(1000);

    @PostConstruct
    public void init(){
        register(SubPolicy.class);
    }

    public String getPolicyUuidBySubPolicyUuid(String subPolicyUuid){
        String policyUuid = null;
        for(String key : policyUuidToSubPolicyMap.keySet()){
            Set<String> set = policyUuidToSubPolicyMap.get(key);
            if(set.contains(subPolicyUuid)){
                policyUuid = key;
                break;
            }
        }
        return policyUuid;
    }

    @Override
    public SubPolicy get(String uuid){
        return subPolicyMap.get(uuid);
    }

    @Override
    public void put(String uuid, SubPolicy subPolicy){
        subPolicyMap.put(uuid,subPolicy);

        Set subPolicyUuidSet = policyUuidToSubPolicyMap.get(subPolicy.getPolicyUuid());
        if(subPolicyUuidSet == null){
            subPolicyUuidSet = new ConcurrentHashSet();
            Set oldSubPolicyUuidSet = policyUuidToSubPolicyMap.putIfAbsent(subPolicy.getPolicyUuid(),subPolicyUuidSet);
            if(oldSubPolicyUuidSet != null){
                subPolicyUuidSet = oldSubPolicyUuidSet;
            }
        }
        subPolicyUuidSet.add(uuid);
    }

    @Override
    public SubPolicy remove(String uuid){
        SubPolicy subPolicy = subPolicyMap.remove(uuid);

        if(subPolicy == null){
            return null;
        }

        Set subPolicyUuidSet = policyUuidToSubPolicyMap.get(subPolicy.getPolicyUuid());
        subPolicyUuidSet.remove(uuid);

        return subPolicy;
    }


    public List<SubPolicy> getSubPolicyByPolicyUuid(String policyUuid){
        Set<String> subPolicyUuidSet = policyUuidToSubPolicyMap.get(policyUuid);
        if(subPolicyUuidSet == null){
            return null;
        }
        List<SubPolicy> subPolicyList = new ArrayList<>();
        for(String subPolicyUuid:subPolicyUuidSet){
            SubPolicy subPolicy = subPolicyMap.get(subPolicyUuid);
            if(subPolicy != null){
                subPolicyList.add(subPolicy);
            }
        }
        return subPolicyList;
    }
}
