package cn.tongdun.kunpeng.api.engine.model.decisionmode;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 策略执行模式缓存 policyUuid -> AbstractDecisionMode
 * @Author: liang.chen
 * @Date: 2019/12/17 上午11:47
 */
@Component
public class DecisionModeCache extends AbstractLocalCache<String,AbstractDecisionMode>{
    //policyUuid -> AbstractDecisionMode
    private Map<String,AbstractDecisionMode> decisionModeMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        register(AbstractDecisionMode.class);
    }

    @Override
    public AbstractDecisionMode get(String uuid){
        return decisionModeMap.get(uuid);
    }

    @Override
    public void put(String uuid, AbstractDecisionMode decisionMode){
        decisionModeMap.put(uuid,decisionMode);
    }

    @Override
    public AbstractDecisionMode remove(String uuid){
        return decisionModeMap.remove(uuid);
    }
}
