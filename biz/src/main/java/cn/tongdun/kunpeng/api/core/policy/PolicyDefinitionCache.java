package cn.tongdun.kunpeng.api.core.policy;

import cn.tongdun.kunpeng.api.cache.AbstractLocalCache;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 策略定义缓存。
 */
@Data
public class PolicyDefinitionCache extends AbstractLocalCache<String,PolicyDefinition> {

    public static final String SPLIT_CHAR = "^^";
    //policyDefinitionUuid -> PolicyDefinition
    private Map<String,PolicyDefinition> policyDefinitionMap = new ConcurrentHashMap<>(3000);

    //partnerCode^^appName^^eventId -> policyDefinitionUuid
    private Map<String,String> policyDefinitionUuidMap = new ConcurrentHashMap<>(3000);

    @PostConstruct
    public void init(){
        register(PolicyDefinition.class);
    }

    @Override
    public PolicyDefinition get(String uuid){
        return policyDefinitionMap.get(uuid);
    }

    @Override
    public void put(String uuid, PolicyDefinition policy){
        policyDefinitionMap.put(uuid,policy);
    }

    @Override
    public PolicyDefinition remove(String uuid){
        return policyDefinitionMap.remove(uuid);
    }


    /**
     * 根据partner,appname,eventId 三要素取得策略的运行版本policyUuid
     * @param partner
     * @param appname
     * @param eventId
     * @return
     */
    public String getPolicyUuid(String partner, String appname, String eventId){
        String key = buildKey(partner,appname,eventId);
        String policyDefinitionUuid = policyDefinitionUuidMap.get(key);

        if(StringUtils.isBlank(policyDefinitionUuid)){
            return null;
        }

        PolicyDefinition policyDefinition = policyDefinitionMap.get(policyDefinitionUuid);
        if(policyDefinition == null){
            return null;
        }

        return policyDefinition.getCurrVersionUuid();
    }


    private static String buildKey(Policy policy){
        return buildKey(policy.getPartnerCode(),policy.getAppName(),policy.getVersion());
    }

    /**
     * Build key:partnerCode^^appName^^eventId
     */
    private static String buildKey(String partner, String appname, String eventId) {
        return StringUtils.join(partner, SPLIT_CHAR, appname,
                SPLIT_CHAR, eventId);
    }
}