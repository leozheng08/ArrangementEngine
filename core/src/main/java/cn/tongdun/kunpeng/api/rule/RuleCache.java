package cn.tongdun.kunpeng.api.rule;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 规则缓存ruleUuid -> Rule
 * @Author: liang.chen
 * @Date: 2019/12/16 下午8:01
 */
@Component
public class RuleCache {

    //ruleUuid -> Rule
    private Map<String,Rule> ruleMap = new ConcurrentHashMap<>();


    public Rule getRule(String uuid){
        return ruleMap.get(uuid);
    }

    public void putRule(String uuid,Rule rule){
        ruleMap.put(uuid,rule);
    }

    public void removeRule(String uuid){
        ruleMap.remove(uuid);
    }
}
