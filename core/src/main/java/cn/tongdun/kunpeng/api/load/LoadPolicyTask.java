package cn.tongdun.kunpeng.api.load;

import cn.tongdun.kunpeng.api.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.convertor.IConvertor;
import cn.tongdun.kunpeng.api.convertor.IConvertorFactory;
import cn.tongdun.kunpeng.api.dataobject.PolicyDO;
import cn.tongdun.kunpeng.api.dataobject.RuleDO;
import cn.tongdun.kunpeng.api.dataobject.SubPolicyDO;
import cn.tongdun.kunpeng.api.policy.Policy;
import cn.tongdun.kunpeng.api.rule.Rule;
import cn.tongdun.kunpeng.api.runmode.AbstractRunMode;
import cn.tongdun.kunpeng.api.subpolicy.SubPolicy;
import cn.tongdun.tdframework.core.logger.Logger;
import cn.tongdun.tdframework.core.logger.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @Author: liang.chen
 * @Date: 2019/12/18 下午1:45
 */
public class LoadPolicyTask implements Callable<Boolean> {
    private Logger logger = LoggerFactory.getLogger(LoadPolicyTask.class);
    private PolicyDO policyDO;
    private IConvertorFactory convertorFactory;

    private LocalCacheService localCacheService;



    public LoadPolicyTask(PolicyDO policyDO,IConvertorFactory convertorFactory,LocalCacheService localCacheService){
        this.policyDO = policyDO;
        this.convertorFactory = convertorFactory;
        this.localCacheService = localCacheService;
    }

    /**
     * 加载策略到缓存中。
     * 先通过转换器，将数据库对象转换为可运行实体
     * 再将各层的运行实体如规则、子策略、运行模式(并行执行、决策流)、策略 ，保存到本次缓存中。
     * 注意保存实体时，先从最小的对象先保存，最后保存策略缓存。
     * @return
     */
    @Override
    public Boolean call(){

        try {
            List<SubPolicyDO> subPolicyDOList = policyDO.getSubPolicyList();

            IConvertor<PolicyDO, Policy> policyConvertor = convertorFactory.getConvertor(PolicyDO.class);
            Policy policy = policyConvertor.convert(policyDO);

            if (subPolicyDOList != null) {
                for (SubPolicyDO subPolicyDO : subPolicyDOList) {
                    IConvertor<SubPolicyDO, SubPolicy> subPolicyConvertor = convertorFactory.getConvertor(SubPolicyDO.class);
                    SubPolicy subPolicy = subPolicyConvertor.convert(subPolicyDO);
                    if (subPolicyDO.getRules() != null) {
                        for (RuleDO ruleDO : subPolicyDO.getRules()) {
                            IConvertor<RuleDO, Rule> ruleConvertor = convertorFactory.getConvertor(RuleDO.class);
                            Rule rule = ruleConvertor.convert(ruleDO);
                            //缓存规则
                            localCacheService.put(Rule.class,rule.getUuid(),rule);
                        }
                    }
                    //缓存子策略
                    localCacheService.put(SubPolicy.class,subPolicy.getSubPolicyUuid(),subPolicy);
                }
            }

            //缓存运行模式
            localCacheService.put(AbstractRunMode.class,policy.getPolicyUuId(),policy.getRunMode());
            //缓存策略
            localCacheService.put(Policy.class,policy.getPolicyUuId(), policy);
        } catch (Exception e){
            logger.error("LoadPolicyTask error",e);
        }
        return true;
    }
}
