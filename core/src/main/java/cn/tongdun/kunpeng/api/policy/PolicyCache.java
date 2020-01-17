package cn.tongdun.kunpeng.api.policy;

import cn.tongdun.kunpeng.api.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.cache.ILocalCache;
import cn.tongdun.kunpeng.api.subpolicy.SubPolicy;
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

    //partnerCode^^appName^^eventId^^version -> policyUuid
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
     * 根据partner,appname,eventId,version 四要素取得policyUuid
     * @param partner
     * @param appname
     * @param eventId
     * @return
     */
    public String getPolicyUuid(String partner, String appname, String eventId,String version){
        String key = buildKey(partner,appname,eventId,version);
        return policyUuidMap.get(key);
    }


    private static String buildKey(Policy policy){
        return buildKey(policy.getPartnerCode(),policy.getAppName(),policy.getVersion(),policy.getVersion());
    }

    /**
     * Build key:partnerCode^^appName^^eventId^^version
     */
    private static String buildKey(String partner, String appname, String eventId,String version) {
        return StringUtils.join(partner, SPLIT_CHAR, appname,
                SPLIT_CHAR, eventId, SPLIT_CHAR, version);
    }
}
