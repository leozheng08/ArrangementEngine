package cn.tongdun.kunpeng.api.load;

import cn.tongdun.kunpeng.api.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.convertor.IConvertor;
import cn.tongdun.kunpeng.api.convertor.IConvertorFactory;
import cn.tongdun.kunpeng.api.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.dto.RuleDTO;
import cn.tongdun.kunpeng.api.dto.SubPolicyDTO;
import cn.tongdun.kunpeng.api.core.policy.Policy;
import cn.tongdun.kunpeng.api.core.rule.Rule;
import cn.tongdun.kunpeng.api.core.runmode.AbstractRunMode;
import cn.tongdun.kunpeng.api.core.subpolicy.SubPolicy;
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
    private PolicyDTO policyDO;
    private IConvertorFactory convertorFactory;

    private LocalCacheService localCacheService;



    public LoadPolicyTask(PolicyDTO policyDO, IConvertorFactory convertorFactory, LocalCacheService localCacheService){
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
            List<SubPolicyDTO> subPolicyDOList = policyDO.getSubPolicyList();

            IConvertor<PolicyDTO, Policy> policyConvertor = convertorFactory.getConvertor(PolicyDTO.class);
            Policy policy = policyConvertor.convert(policyDO);

            if (subPolicyDOList != null) {
                for (SubPolicyDTO subPolicyDO : subPolicyDOList) {
                    IConvertor<SubPolicyDTO, SubPolicy> subPolicyConvertor = convertorFactory.getConvertor(SubPolicyDTO.class);
                    SubPolicy subPolicy = subPolicyConvertor.convert(subPolicyDO);
                    if (subPolicyDO.getRules() != null) {
                        for (RuleDTO ruleDO : subPolicyDO.getRules()) {
                            IConvertor<RuleDTO, Rule> ruleConvertor = convertorFactory.getConvertor(RuleDTO.class);
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
