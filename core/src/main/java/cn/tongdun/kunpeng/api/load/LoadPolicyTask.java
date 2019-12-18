package cn.tongdun.kunpeng.api.load;

import cn.tongdun.kunpeng.api.convertor.IConvertor;
import cn.tongdun.kunpeng.api.convertor.IConvertorFactory;
import cn.tongdun.kunpeng.api.dataobject.PolicyDO;
import cn.tongdun.kunpeng.api.dataobject.RuleDO;
import cn.tongdun.kunpeng.api.dataobject.SubPolicyDO;
import cn.tongdun.kunpeng.api.policy.Policy;
import cn.tongdun.kunpeng.api.policy.PolicyCache;
import cn.tongdun.kunpeng.api.rule.Rule;
import cn.tongdun.kunpeng.api.rule.RuleCache;
import cn.tongdun.kunpeng.api.runmode.RunModeCache;
import cn.tongdun.kunpeng.api.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.api.subpolicy.SubPolicyCache;
import cn.tongdun.tdframework.core.logger.Logger;
import cn.tongdun.tdframework.core.logger.LoggerFactory;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;

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
    private PolicyCache policyCache;
    private SubPolicyCache subPolicyCache;
    private RuleCache ruleCache;
    private RunModeCache runModeCache;


    public LoadPolicyTask(PolicyDO policyDO,IConvertorFactory convertorFactory,PolicyCache policyCache,
                          SubPolicyCache subPolicyCache,RuleCache ruleCache,RunModeCache runModeCache){
        this.policyDO = policyDO;
        this.convertorFactory = convertorFactory;
        this.policyCache = policyCache;
        this.subPolicyCache = subPolicyCache;
        this.ruleCache = ruleCache;
        this.runModeCache = runModeCache;
    }

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
                            ruleCache.putRule(rule.getUuid(), rule);
                        }
                    }

                    subPolicyCache.putSubPolicy(subPolicy.getSubPolicyUuid(), subPolicy);
                }
            }

            runModeCache.putRunMode(policy.getPolicyUuId(),policy.getRunMode());

            policyCache.putPolicy(policy.getPolicyUuId(), policy);

        } catch (Exception e){
            logger.error("LoadPolicyTask error",e);
        }
        return true;
    }
}
