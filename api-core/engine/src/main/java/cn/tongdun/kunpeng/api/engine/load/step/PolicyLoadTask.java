package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.cache.BatchRemoteCallDataCache;
import cn.tongdun.kunpeng.api.engine.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertorFactory;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataManager;
import cn.tongdun.kunpeng.api.engine.dto.*;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.IPlatformIndexRepository;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.PlatformIndexCache;
import cn.tongdun.kunpeng.api.engine.model.customoutput.PolicyCustomOutput;
import cn.tongdun.kunpeng.api.engine.model.customoutput.PolicyCustomOutputCache;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.AbstractDecisionMode;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionFlow;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionModeType;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.ParallelSubPolicy;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policyfieldencryption.PolicyFieldEncryption;
import cn.tongdun.kunpeng.api.engine.model.policyfieldencryption.PolicyFieldEncryptionCache;
import cn.tongdun.kunpeng.api.engine.model.policyfieldnecessary.PolicyFieldNecessary;
import cn.tongdun.kunpeng.api.engine.model.policyfieldnecessary.PolicyFieldNecessaryCache;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndex;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndexCache;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.client.dto.DecisionFlowDTO;
import cn.tongdun.kunpeng.client.dto.RuleDTO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

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

    private IPlatformIndexRepository platformIndexRepository;

    private PlatformIndexCache platformIndexCache;

    private BatchRemoteCallDataCache batchRemoteCallDataCache;

    private PolicyCustomOutputCache outputCache;

    /**
     * 加密字段的缓存
     */
    private PolicyFieldEncryptionCache fieldEncryptionCache;

    /**
     * 必传字段的缓存
     */
    private PolicyFieldNecessaryCache fieldNecessaryCache;

    private PolicyIndexCache policyIndexCache;



    public PolicyLoadTask(String policyUuid, IPolicyRepository policyRepository, IConvertorFactory convertorFactory, LocalCacheService localCacheService,
                          IPlatformIndexRepository platformIndexRepository, PlatformIndexCache platformIndexCache, BatchRemoteCallDataCache batchRemoteCallDataCache, PolicyCustomOutputCache outputCache, PolicyFieldNecessaryCache fieldNecessaryCache
    , PolicyFieldEncryptionCache fieldEncryptionCache,PolicyIndexCache policyIndexCache) {
        this.policyUuid = policyUuid;
        this.convertorFactory = convertorFactory;
        this.localCacheService = localCacheService;
        this.policyRepository = policyRepository;
        this.platformIndexRepository = platformIndexRepository;
        this.platformIndexCache = platformIndexCache;
        this.batchRemoteCallDataCache = batchRemoteCallDataCache;
        this.outputCache = outputCache;
        this.fieldEncryptionCache = fieldEncryptionCache;
        this.fieldNecessaryCache = fieldNecessaryCache;
        this.policyIndexCache = policyIndexCache;
    }

    /**
     * 加载策略到缓存中。
     * 先通过转换器，将数据库对象转换为可运行实体
     * 再将各层的运行实体如规则、子策略、运行模式(并行执行、决策流)、策略 ，保存到本次缓存中。
     * 注意保存实体时，先从最小的对象先保存，最后保存策略缓存。
     *
     * @return
     */
    @Override
    public Boolean call() {
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
                            localCacheService.put(Rule.class, rule.getUuid(), rule);
                            //缓存规则中批量远程调用相关的数据
                            this.addBatchRemoteCallDataToCache(subPolicy.getUuid(), ruleDO);
                        }
                    }
                    //缓存子策略
                    localCacheService.put(SubPolicy.class, subPolicy.getUuid(), subPolicy);

                    //策略指标
                    if (null != subpolicyDTO.getIndexDefinitionList() && !subpolicyDTO.getIndexDefinitionList().isEmpty()) {
                        indexDefinitionDTOList.addAll(subpolicyDTO.getIndexDefinitionList());
                    }
                }
            }

            //缓存策略指标
            if (!indexDefinitionDTOList.isEmpty()) {
                IConvertor<List<IndexDefinitionDTO>, List<PolicyIndex>> policyIndexConvertor = convertorFactory.getConvertor(IndexDefinitionDTO.class);
                List<PolicyIndex> policyIndexList = policyIndexConvertor.convert(indexDefinitionDTOList);
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(policyIndexList)) {
                    Map<String,PolicyIndex> policyIndexMap = policyIndexList.stream().collect(Collectors.toMap(PolicyIndex::getUuid, policyIndex -> policyIndex));
                    policyIndexCache.put(policyDTO.getUuid(),policyIndexMap);
                }
            }


            PolicyDecisionModeDTO policyDecisionModeDTO = policyDTO.getPolicyDecisionModeDTO();
            if (policyDecisionModeDTO != null && DecisionModeType.FLOW.name().equalsIgnoreCase(policyDecisionModeDTO.getDecisionModeType())
                    && policyDTO.getDecisionFlowDTO() != null) {
                //策略流运行模式
                IConvertor<DecisionFlowDTO, DecisionFlow> flowConvertor = convertorFactory.getConvertor(DecisionFlowDTO.class);
                DecisionFlow decisionFlow = flowConvertor.convert(policyDTO.getDecisionFlowDTO());
                policy.setDecisionMode(decisionFlow);
            } else {
                //策略运行模式
                ParallelSubPolicy parallelSubPolicy = new ParallelSubPolicy();
                parallelSubPolicy.setPolicyUuid(policy.getUuid());
                parallelSubPolicy.setGmtModify(policyDecisionModeDTO != null ? policyDecisionModeDTO.getGmtModify() : policy.getGmtModify());
                policy.setDecisionMode(parallelSubPolicy);

            }

            //加载平台指标
            List<String> gaeaIds = platformIndexRepository.queryByPolicyUuid(policyUuid);
            if (!CollectionUtils.isEmpty(gaeaIds)) {
                platformIndexCache.putList(policyUuid, gaeaIds);
            }

            //缓存运行模式
            localCacheService.put(AbstractDecisionMode.class, policy.getUuid(), policy.getDecisionMode());
            //缓存策略
            localCacheService.put(Policy.class, policy.getUuid(), policy);
            //缓存自定义输出，平台层有默认实现，不会进行缓存
            List<PolicyCustomOutputDTO> list = policyDTO.getPolicyCustomOutputDTOList();
            if (!CollectionUtils.isEmpty(list)) {
                for (PolicyCustomOutputDTO dto :
                        list) {
                    if (dto.isDeleted()) {
                        continue;
                    }
                    IConvertor<PolicyCustomOutputDTO, PolicyCustomOutput> convertor = convertorFactory.getConvertor(PolicyCustomOutputDTO.class);
                    PolicyCustomOutput output = convertor.convert(dto);
                    //缓存自定义输出
                    List<PolicyCustomOutput> policyCustomOutputList = outputCache.get(output.getPolicyUuid());
                    if (null == policyCustomOutputList) {
                        policyCustomOutputList = new ArrayList<>();
                    }
                    //缓存规则
                    if (output.isConditionConfig()) {
                        Rule rule = output.getRule();
                        localCacheService.put(Rule.class, rule.getUuid(), rule);
                        output.setRuleUuid(rule.getUuid());
                    }
                    policyCustomOutputList.add(output);
                    //key:策略uuid   value:该策略uuid下所有自定义输出对象
                    outputCache.put(output.getPolicyUuid(), policyCustomOutputList);
                }
            }
            // 缓存加密字段
            List<PolicyFieldEncryptionDTO> policyFieldEncryptionDTOList = policyDTO.getPolicyFieldEncryptionDTOList();
            List<PolicyFieldEncryption> policyFieldEncryptionList = new ArrayList<>();
            IConvertor<PolicyFieldEncryptionDTO, PolicyFieldEncryption> fieldEncryptionIConvertor = convertorFactory.getConvertor(PolicyFieldEncryptionDTO.class);
            if (!CollectionUtils.isEmpty(policyFieldEncryptionDTOList)) {
                for (PolicyFieldEncryptionDTO policyFieldEncryptionDTO : policyFieldEncryptionDTOList) {
                    PolicyFieldEncryption policyFieldEncryption = fieldEncryptionIConvertor.convert(policyFieldEncryptionDTO);
                    policyFieldEncryptionList.add(policyFieldEncryption);
                }
                // key：策略集uuid value：该策略uuid下所有的加密字段
                fieldEncryptionCache.put(policyDTO.getPolicyDefinitionUuid(), policyFieldEncryptionList);
            }

            // 缓存必传参数
            IConvertor<PolicyFieldNecessaryDTO, PolicyFieldNecessary> fieldNecessaryIConvertor = convertorFactory.getConvertor(PolicyFieldNecessaryDTO.class);
            List<PolicyFieldNecessaryDTO> policyFieldNecessaryDTOList = policyDTO.getPolicyFieldNecessaryDTOList();
            List<PolicyFieldNecessary> policyFieldNecessaryList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(policyFieldNecessaryDTOList)) {
                for (PolicyFieldNecessaryDTO policyFieldNecessaryDTO : policyFieldNecessaryDTOList) {
                    PolicyFieldNecessary policyFieldNecessary = fieldNecessaryIConvertor.convert(policyFieldNecessaryDTO);
                    policyFieldNecessaryList.add(policyFieldNecessary);
                }
                // key：策略集uuid value：该策略uuid下所有的必传字段
                fieldNecessaryCache.put(policyDTO.getPolicyDefinitionUuid(), policyFieldNecessaryList);
            }

        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "LoadPolicyTask error, policyUuid:{}, partnerCode:{}, eventId:{}",
                    policyUuid, policyDTO != null ? policyDTO.getPartnerCode() : "", policyDTO != null ? policyDTO.getEventId() : "",
                    e);
            return false;
        }
        return true;
    }

    /**
     * 添加/更新批量调用数据到cache
     *
     * @param subPolicyUuid
     * @param ruleDTO
     */
    private void addBatchRemoteCallDataToCache(String subPolicyUuid, RuleDTO ruleDTO) {
        Map<String, List<Object>> batchdatas = BatchRemoteCallDataManager.buildData(policyUuid, subPolicyUuid, ruleDTO);
        if (!CollectionUtils.isEmpty(batchdatas)) {
            Set<Map.Entry<String, List<Object>>> entries = batchdatas.entrySet();
            for (Map.Entry<String, List<Object>> entry : entries) {
                batchRemoteCallDataCache.addOrUpdate(policyUuid, entry.getKey(), ruleDTO.getUuid(), entry.getValue());
            }
        }
    }
}
