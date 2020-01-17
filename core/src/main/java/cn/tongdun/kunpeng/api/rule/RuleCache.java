package cn.tongdun.kunpeng.api.rule;

import cn.tongdun.kunpeng.api.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.cache.ILocalCache;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 规则缓存ruleUuid -> Rule
 * @Author: liang.chen
 * @Date: 2019/12/16 下午8:01
 */
@Component
public class RuleCache extends AbstractLocalCache<String,Rule> {

    @PostConstruct
    public void init(){
        register(Rule.class);
    }

    //ruleUuid -> Rule
    private Map<String,Rule> ruleMap = new ConcurrentHashMap<>();

    @Override
    public Rule get(String uuid){
        return ruleMap.get(uuid);
    }

    @Override
    public void put(String uuid, Rule rule){
        ruleMap.put(uuid,rule);
    }

    @Override
    public Rule remove(String uuid){
        return ruleMap.remove(uuid);
    }
}
