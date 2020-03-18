package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertorFactory;
import cn.tongdun.kunpeng.api.engine.dto.*;
import cn.tongdun.kunpeng.api.engine.dto.IndexDefinitionDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDTO;
import cn.tongdun.kunpeng.client.dto.RuleDTO;
import cn.tongdun.kunpeng.api.engine.dto.SubPolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.IPlatformIndexRepository;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.PlatformIndexCache;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionModeType;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndex;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.AbstractDecisionMode;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionFlow;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.ParallelSubPolicy;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.client.dto.DecisionFlowDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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

    private IPlatformIndexRepository policyIndicatrixItemRepository;

    private PlatformIndexCache policyIndicatrixItemCache;



    public PolicyLoadTask(String policyUuid, IPolicyRepository policyRepository, IConvertorFactory convertorFactory, LocalCacheService localCacheService,
                          IPlatformIndexRepository policyIndicatrixItemRepository, PlatformIndexCache policyIndicatrixItemCache){
        this.policyUuid = policyUuid;
        this.convertorFactory = convertorFactory;
        this.localCacheService = localCacheService;
        this.policyRepository = policyRepository;
        this.policyIndicatrixItemRepository = policyIndicatrixItemRepository;
        this.policyIndicatrixItemCache = policyIndicatrixItemCache;
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
            policyDTO = policyRepository.queryFullByUuid(policyUuid);

            List<SubPolicyDTO> subPolicyDTOList = policyDTO.getSubPolicyList();
            List<IndexDefinitionDTO> indexDefinitionDTOList = new ArrayList<>();

            IConvertor<PolicyDTO, Policy> policyConvertor = convertorFactory.getConvertor(PolicyDTO.class);
            Policy policy = policyConvertor.convert(policyDTO);

            if (subPolicyDTOList != null) {
                for (SubPolicyDTO subpolicyDTO : subPolicyDTOList) {
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

                    //策略指标
                    if (null!=subpolicyDTO.getIndexDefinitionList()&&!subpolicyDTO.getIndexDefinitionList().isEmpty()){
                        indexDefinitionDTOList.addAll(subpolicyDTO.getIndexDefinitionList());
                    }
                }
            }

            //缓存策略指标
            if (!indexDefinitionDTOList.isEmpty()){
                IConvertor<List<IndexDefinitionDTO>, List<PolicyIndex>> policyIndexConvertor=convertorFactory.getConvertor(IndexDefinitionDTO.class);
                List<PolicyIndex> policyIndexList=policyIndexConvertor.convert(indexDefinitionDTOList);
                if (null!=policyIndexList&&!policyIndexList.isEmpty()){
                    localCacheService.putList(PolicyIndex.class,policyDTO.getUuid(),policyIndexList);
                }
            }


            PolicyDecisionModeDTO policyDecisionModeDTO = policyDTO.getPolicyDecisionModeDTO();
            if(policyDecisionModeDTO != null && DecisionModeType.FLOW.name().equalsIgnoreCase(policyDecisionModeDTO.getDecisionModeType())
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

            //加载平台指标
            List<String> gaeaIds = policyIndicatrixItemRepository.queryByPolicyUuid(policyUuid);
            if (!CollectionUtils.isEmpty(gaeaIds)) {
                policyIndicatrixItemCache.putList(policyUuid, gaeaIds);
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
