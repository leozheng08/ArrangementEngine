package cn.tongdun.kunpeng.api.engine.model.policy;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import org.apache.commons.lang3.StringUtils;
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

    public static final String SPLIT_CHAR = "^^";

    //policyUuid -> Policy
    private Map<String,Policy> policyMap = new ConcurrentHashMap<>(5000);

    //partnerCode^^eventId^^version -> policyUuid
    private Map<String,String> policyUuidMap = new ConcurrentHashMap<>(5000);


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
        policyUuidMap.put(buildKey(policy),uuid);
    }

    @Override
    public Policy remove(String uuid){
        Policy policy = policyMap.remove(uuid);
        if(policy == null){
            return null;
        }

        policyUuidMap.remove(buildKey(policy));
        return policy;
    }




    /**
     * 根据partner,eventId,version 四要素取得policyUuid
     * @param partner
     * @param eventId
     * @return
     */
    public String getPolicyUuid(String partner, String eventId,String version){
        String key = buildKey(partner,eventId,version);
        return policyUuidMap.get(key);
    }


    private static String buildKey(Policy policy){
        return buildKey(policy.getPartnerCode(),policy.getEventId(),policy.getVersion());
    }

    /**
     * Build key:partnerCode^^eventId^^version
     */
    private static String buildKey(String partner, String eventId,String version) {
        return StringUtils.join(StringUtils.trim(partner), SPLIT_CHAR, StringUtils.trim(eventId), SPLIT_CHAR, StringUtils.trim(version));
    }
}
