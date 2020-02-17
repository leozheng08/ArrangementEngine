package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertorFactory;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.engine.dto.RuleDTO;
import cn.tongdun.kunpeng.api.engine.dto.SubPolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.runmode.AbstractRunMode;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @Author: liang.chen
 * @Date: 2019/12/18 下午1:45
 */
public class PolicyLoadTask implements Callable<Boolean> {
    private Logger logger = LoggerFactory.getLogger(PolicyLoadTask.class);
    private String policyUuid;
    private IConvertorFactory convertorFactory;

    private LocalCacheService localCacheService;

    private IPolicyRepository policyRepository;



    public PolicyLoadTask(String policyUuid, IPolicyRepository policyRepository, IConvertorFactory convertorFactory, LocalCacheService localCacheService){
        this.policyUuid = policyUuid;
        this.convertorFactory = convertorFactory;
        this.localCacheService = localCacheService;
        this.policyRepository = policyRepository;
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
            PolicyDTO policyDO = policyRepository.queryByUuid(policyUuid);

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
                    localCacheService.put(SubPolicy.class,subPolicy.getUuid(),subPolicy);
                }
            }

            //缓存运行模式
            localCacheService.put(AbstractRunMode.class,policy.getUuid(),policy.getRunMode());
            //缓存策略
            localCacheService.put(Policy.class,policy.getUuid(), policy);
        } catch (Exception e){
            logger.error("LoadPolicyTask error",e);
        }
        return true;
    }
}
