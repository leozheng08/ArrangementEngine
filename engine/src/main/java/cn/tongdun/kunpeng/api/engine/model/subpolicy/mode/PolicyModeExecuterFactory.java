package cn.tongdun.kunpeng.api.engine.model.subpolicy.mode;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.client.data.PolicyMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: liang.chen
 * @Date: 2020/4/15 下午4:51
 */
@Component
public class PolicyModeExecuterFactory extends AbstractLocalCache<PolicyMode,AbstractPolicyModeExecuter> {
    //policyMode -> AbstractSubPolicyMode
    private Map<PolicyMode,AbstractPolicyModeExecuter> policyModeMap = new ConcurrentHashMap<>(3);

    @Autowired
    private WeightedPolicyModeExecuter weightedPolicyModeExecuter;

    @PostConstruct
    public void init(){
        register(AbstractPolicyModeExecuter.class);
    }

    @Override
    public AbstractPolicyModeExecuter get(PolicyMode policyMode){
        return policyModeMap.get(policyMode);
    }

    @Override
    public void put(PolicyMode policyMode, AbstractPolicyModeExecuter abstractSubPolicyMode){
        policyModeMap.put(policyMode,abstractSubPolicyMode);
    }

    @Override
    public AbstractPolicyModeExecuter remove(PolicyMode policyMode){
        return policyModeMap.remove(policyMode);
    }


    public AbstractPolicyModeExecuter getDefaultExecuter(){
        return weightedPolicyModeExecuter;
    }
}
