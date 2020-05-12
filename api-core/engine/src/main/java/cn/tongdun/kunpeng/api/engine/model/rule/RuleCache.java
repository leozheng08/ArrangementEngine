package cn.tongdun.kunpeng.api.engine.model.rule;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 规则缓存ruleUuid -> Rule
 * @Author: liang.chen
 * @Date: 2019/12/16 下午8:01
 */
@Component
public class RuleCache extends AbstractLocalCache<String,Rule> {

    public static final String SPLIT_CHAR = "^^";

    @PostConstruct
    public void init(){
        register(Rule.class);
    }

    //ruleUuid -> Rule
    private Map<String,Rule> ruleMap = new ConcurrentHashMap<>(5000);

    //subPolicyUuid -> Set<ruleUuid>
    private Map<String,Set<String>>  subPolicyUuidToRuleMap = new ConcurrentHashMap<>(3000);

    @Override
    public Rule get(String uuid){
        return ruleMap.get(uuid);
    }

    @Override
    public void put(String uuid, Rule rule){
        ruleMap.put(uuid,rule);

        String key = buildKey(rule.getBizType(),rule.getBizUuid());
        Set ruleUuidSet = subPolicyUuidToRuleMap.get(key);
        if(ruleUuidSet == null){
            ruleUuidSet = new ConcurrentHashSet();
            Set oldRuleUuidSet = subPolicyUuidToRuleMap.putIfAbsent(key,ruleUuidSet);
            if(oldRuleUuidSet != null){
                ruleUuidSet = oldRuleUuidSet;
            }
        }
        ruleUuidSet.add(uuid);
    }

    @Override
    public Rule remove(String uuid){
        Rule rule = ruleMap.remove(uuid);
        if(rule == null){
            return null;
        }

        String key = buildKey(rule.getBizType(),rule.getBizUuid());
        Set ruleUuidSet = subPolicyUuidToRuleMap.get(key);
        ruleUuidSet.remove(uuid);

        return rule;
    }

    private static String buildKey(String bizType, String bizUuid) {
        return StringUtils.join(bizType, SPLIT_CHAR, bizUuid);
    }



    public List<Rule> getRuleBySubPolicyUuid(String subPolicyUuid){
        Set<String> ruleUuidSet = subPolicyUuidToRuleMap.get(buildKey("sub_policy",subPolicyUuid));
        if(ruleUuidSet == null){
            return null;
        }
        List<Rule> ruleList = new ArrayList<>();
        for(String ruleUuid:ruleUuidSet){
            Rule rule = ruleMap.get(ruleUuid);
            if(rule != null){
                ruleList.add(rule);
            }
        }
        return ruleList;
    }
}
