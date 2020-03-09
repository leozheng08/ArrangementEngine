package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertorFactory;
import cn.tongdun.kunpeng.api.engine.dto.*;
import cn.tongdun.kunpeng.api.engine.dto.IndexDefinitionDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.engine.dto.RuleDTO;
import cn.tongdun.kunpeng.api.engine.dto.SubPolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndex;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.AbstractDecisionMode;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionFlow;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.ParallelSubPolicy;
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
    private static Logger logger = LoggerFactory.getLogger(PolicyLoadTask.class);
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
        PolicyDTO policyDTO = null;
        try {
            policyDTO = policyRepository.queryByUuid(policyUuid);

            List<SubPolicyDTO> subpolicyDTOList = policyDTO.getSubPolicyList();

            IConvertor<PolicyDTO, Policy> policyConvertor = convertorFactory.getConvertor(PolicyDTO.class);
            Policy policy = policyConvertor.convert(policyDTO);

            if (subpolicyDTOList != null) {
                for (SubPolicyDTO subpolicyDTO : subpolicyDTOList) {
                    IConvertor<SubPolicyDTO, SubPolicy> subPolicyConvertor = convertorFactory.getConvertor(SubPolicyDTO.class);
                    SubPolicy subPolicy = subPolicyConvertor.convert(subpolicyDTO);
                    if (subpolicyDTO.getRules() != null) {
                        for (RuleDTO ruleDO : subpolicyDTO.getRules()) {
                            IConvertor<RuleDTO, Rule> ruleConvertor = convertorFactory.getConvertor(RuleDTO.class);
                            Rule rule = ruleConvertor.convert(ruleDO);
                            //缓存规则
                            localCacheService.put(Rule.class,rule.getUuid(),rule);
                        }
                    }
                    //缓存子策略
                    localCacheService.put(SubPolicy.class,subPolicy.getUuid(),subPolicy);

                    //缓存策略指标
                    if (null!=subpolicyDTO.getIndexDefinitionList()&&!subpolicyDTO.getIndexDefinitionList().isEmpty()){
                        IConvertor<List<IndexDefinitionDTO>, List<PolicyIndex>> policyIndexConvertor=convertorFactory.getConvertor(IndexDefinitionDTO.class);
                        List<PolicyIndex> policyIndexList=policyIndexConvertor.convert(subpolicyDTO.getIndexDefinitionList());
                        if (null!=policyIndexList&&!policyIndexList.isEmpty()){
                            localCacheService.putList(PolicyIndex.class,policyDTO.getUuid(),policyIndexList);
                        }
                    }

                }
            }

            PolicyDecisionModeDTO policyDecisionModeDTO = policyDTO.getPolicyDecisionModeDTO();
            if(policyDecisionModeDTO != null && "flow".equals(policyDecisionModeDTO.getDecisionModeType())
                && policyDTO.getDecisionFlowDTO() != null){
                //策略流运行模式
                IConvertor<DecisionFlowDTO, DecisionFlow> flowConvertor = convertorFactory.getConvertor(DecisionFlowDTO.class);
                DecisionFlow decisionFlow = flowConvertor.convert(policyDTO.getDecisionFlowDTO());
                policy.setDecisionMode(decisionFlow);
            } else {
                //策略运行模式
                ParallelSubPolicy parallelSubPolicy = new ParallelSubPolicy();
                parallelSubPolicy.setPolicyUuid(policy.getUuid());
                policy.setDecisionMode(parallelSubPolicy);

            }



            //缓存运行模式
            localCacheService.put(AbstractDecisionMode.class,policy.getUuid(),policy.getDecisionMode());
            //缓存策略
            localCacheService.put(Policy.class,policy.getUuid(), policy);
        } catch (Exception e){
            logger.error("LoadPolicyTask error, policyUuid:{}, partnerCode:{}, eventId:{}",
                    policyUuid, policyDTO!=null?policyDTO.getPartnerCode():"",policyDTO != null? policyDTO.getEventId():"",
                    e);
            return false;
        }
        return true;
    }
}
