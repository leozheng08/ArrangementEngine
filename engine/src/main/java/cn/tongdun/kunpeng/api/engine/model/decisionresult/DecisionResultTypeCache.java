package cn.tongdun.kunpeng.api.engine.model.decisionresult;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/2/21 上午1:34
 */
@Component
@Data
public class DecisionResultTypeCache extends AbstractLocalCache<String,DecisionResultType> {

    //决策结果的类型，如Accept、Review、Reject.当前先固定三种，后继如果有决策结果定义表，再从数据库加载
    //code -> DecisionResultType
    private Map<String,DecisionResultType> decisionResultMap = new LinkedHashMap<String,DecisionResultType>(){{
        put("Accept",new DecisionResultType("Accept","通过",1,false));
        put("Review",new DecisionResultType("Review","人工审核",2,true));
        put("Reject",new DecisionResultType("Reject","拒绝",3,true));
    }};


    @PostConstruct
    public void init(){
        register(DecisionResultType.class);
    }

    @Override
    public DecisionResultType get(String code){
        return decisionResultMap.get(code);
    }

    @Override
    public void put(String code, DecisionResultType subPolicy){
        decisionResultMap.put(code,subPolicy);
    }

    @Override
    public DecisionResultType remove(String code){
        return decisionResultMap.remove(code);
    }

    public DecisionResultType getDefaultType(){
        return decisionResultMap.values().iterator().next();
    }

    public Collection<DecisionResultType> getDecisionResultTypes(){
        return decisionResultMap.values();
    }
}
