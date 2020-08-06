package cn.tongdun.kunpeng.api.engine.model.policy.challenger;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyManager;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 子策略缓存subPolicyUuid -> SubPolicy
 * @Author: liang.chen
 * @Date: 2019/12/16 下午8:01
 */
@Component
public class PolicyChallengerCache extends AbstractLocalCache<String,PolicyChallenger> {
    private static Logger logger = LoggerFactory.getLogger(PolicyChallengerCache.class);
    //policyDefinitionUuid -> PolicyChallenger
    private Map<String,PolicyChallenger> policyChallengerMap = new ConcurrentHashMap<>(20);

    //policyUuid -> PolicyChallenger.Config
    private Map<String,PolicyChallenger.Config> policyChallengerConfigMap = new ConcurrentHashMap<>(20);

    //policyDefinitionUuid -> WeightRoundRobin 权重轮询
    private Map<String,WeightRoundRobin>  weightRoundRobinMap = new ConcurrentHashMap<>(20);

    @PostConstruct
    public void init(){
        register(PolicyChallenger.class);
    }

    @Override
    public PolicyChallenger get(String policyDefinitionUuid){
        return policyChallengerMap.get(policyDefinitionUuid);
    }

    @Override
    public void put(String policyDefinitionUuid, PolicyChallenger policyChallenger){
        policyChallengerMap.put(policyDefinitionUuid,policyChallenger);

        for(PolicyChallenger.Config config: policyChallenger.getChallengerConfig()){
            policyChallengerConfigMap.put(config.getVersionUuid(), config);
        }

        List<Integer> weights = new ArrayList<>();
        if (!checkConfig(policyChallenger.getChallengerConfig(), weights)) {
            return ;
        }
        WeightRoundRobin weightRoundRobin = new WeightRoundRobin();
        weightRoundRobin.init(weights);
        weightRoundRobinMap.put(policyDefinitionUuid, weightRoundRobin);
    }

    public void add(PolicyChallenger policyChallenger){
        put(policyChallenger.getPolicyDefinitionUuid(),policyChallenger);
    }

    @Override
    public PolicyChallenger remove(String policyDefinitionUuid){
        return policyChallengerMap.remove(policyDefinitionUuid);
    }

    private List<PolicyChallenger.Config> getConfigs(String policyDefinitionUuid){
        PolicyChallenger policyChallenger = policyChallengerMap.get(policyDefinitionUuid);
        if(policyChallenger == null){
            return null;
        }
        return policyChallenger.getChallengerConfig();
    }

    public PolicyChallenger.Config getConfig(String policyUuid){
        PolicyChallenger.Config config = policyChallengerConfigMap.get(policyUuid);
        return config;
    }

    /**
     * 根据冠军与挑战者的比率取得要运行的策略uuid
     * @param policyDefinitionUuid
     * @return
     */
    public String getNextPolicyUuid(String policyDefinitionUuid) {
        PolicyChallenger policyChallenger = policyChallengerMap.get(policyDefinitionUuid);
        if(policyChallenger == null){
            return null;
        }
        //还未生效
        if(policyChallenger.getStartTime() != null && System.currentTimeMillis()<policyChallenger.getStartTime().getTime()){
            return null;
        }
        //已失效
        if(policyChallenger.getEndTime() != null && System.currentTimeMillis()>policyChallenger.getEndTime().getTime()){
            return null;
        }

        List<PolicyChallenger.Config> configs = policyChallenger.getChallengerConfig();
        if (CollectionUtils.isEmpty(configs)) {
            return null;
        }

        WeightRoundRobin weightRoundRobin = weightRoundRobinMap.get(policyDefinitionUuid);
        if (weightRoundRobin == null) {
            return null;
        }

        return configs.get(weightRoundRobin.get()).getVersionUuid();
    }


    private boolean checkConfig(List<PolicyChallenger.Config> configs, List<Integer> weights) {
        /* config值示例，其中versionUuid为策略（旧名：策略集）uuid
           config中的元素数目必须大于0.
        [
            {
                "versionUuid": "3333",
                "ratio": 90
            },
            {
                "versionUuid": "4444",
                "ratio": 10
            }
        ]
         */
        if(configs == null || configs.isEmpty()){
            logger.error(TraceUtils.getFormatTrace()+"the size of policy_challenger' config must greater than zero!");
            return false;
        }

        configs.forEach(item -> weights.add(item.getRatio()));
        return true;
    }

}
