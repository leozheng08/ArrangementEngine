package cn.tongdun.kunpeng.api.engine.model.policy.definition;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 策略定义缓存。
 */
@Component
@Data
public class PolicyDefinitionCache extends AbstractLocalCache<String,PolicyDefinition> {

    public static final String SPLIT_CHAR = "^^";
    //policyDefinitionUuid -> PolicyDefinition
    private Map<String,PolicyDefinition> policyDefinitionMap = new ConcurrentHashMap<>(3000);

    //partnerCode^^eventId -> policyDefinitionUuid
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
    public void put(String uuid, PolicyDefinition policyDefinition){
        policyDefinitionMap.put(uuid,policyDefinition);
        policyDefinitionUuidMap.put(buildKey(policyDefinition),uuid);
    }

    @Override
    public PolicyDefinition remove(String uuid){

        PolicyDefinition policyDefinition = policyDefinitionMap.remove(uuid);
        if(policyDefinition != null) {
            policyDefinitionUuidMap.remove(buildKey(policyDefinition));
        }
        return policyDefinition;
    }


    /**
     * 根据partner,eventId 三要素取得策略的运行版本policyUuid
     * @param partner
     * @param eventId
     * @return
     */
    public String getPolicyUuid(String partner, String eventId){
        String key = buildKey(partner,eventId);
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


    private static String buildKey(PolicyDefinition policyDefinition){
        return buildKey(policyDefinition.getPartnerCode(),policyDefinition.getEventId());
    }

    /**
     * Build key:partnerCode^^eventId
     */
    private static String buildKey(String partner, String eventId) {
        return StringUtils.join(StringUtils.trim(partner), SPLIT_CHAR, StringUtils.trim(eventId));
    }
}