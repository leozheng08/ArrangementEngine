package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.cache.BatchRemoteCallDataCache;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilderFactory;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataManager;
import cn.tongdun.kunpeng.api.engine.convertor.impl.RuleConvertor;
import cn.tongdun.kunpeng.api.engine.convertor.impl.SubPolicyConvertor;
import cn.tongdun.kunpeng.api.engine.dto.SubPolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.rule.IRuleRepository;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.ISubPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.SubPolicyEventDO;
import cn.tongdun.kunpeng.client.dto.RuleDTO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
public class SubPolicyReLoadManager implements IReload<SubPolicyEventDO> {

    private Logger logger = LoggerFactory.getLogger(SubPolicyReLoadManager.class);

    @Autowired
    private ISubPolicyRepository subPolicyRepository;

    @Autowired
    private IRuleRepository ruleRepository;

    @Autowired
    private SubPolicyCache subPolicyCache;

    @Autowired
    private SubPolicyConvertor subPolicyConvertor;

    @Autowired
    private ReloadFactory reloadFactory;

    @Autowired
    private RuleCache ruleCache;

    @Autowired
    private PolicyIndicatrixItemReloadManager policyIndicatrixItemReloadManager;

    @Autowired
    private PolicyFieldReloadManager policyFieldReloadManager;

    @Autowired
    private BatchRemoteCallDataCache batchRemoteCallDataCache;

    @Autowired
    private RuleConvertor ruleConvertor;

    @PostConstruct
    public void init() {
        reloadFactory.register(SubPolicyEventDO.class, this);
    }

    @Override
    public boolean create(SubPolicyEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean update(SubPolicyEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean activate(SubPolicyEventDO eventDO) {
        return addOrUpdate(eventDO);
    }


    @Override
    public boolean imported(SubPolicyEventDO eventDO) {
        String uuid = eventDO.getUuid();
        logger.debug(TraceUtils.getFormatTrace() + "SubPolicy reload start, uuid:{}", uuid);
        try {
            List<RuleDTO> ruleDOList = ruleRepository.queryFullBySubPolicyUuid(uuid);
            // 这里的RuleDTO都是开启的
            for (RuleDTO ruleDTO : ruleDOList) {
                Rule newRule = ruleConvertor.convert(ruleDTO);
                ruleCache.put(ruleDTO.getUuid(), newRule);
                //处理需要批量远程调用的数据
                this.addBatchRemoteCallDataToCache(ruleDTO.getPolicyUuid(), ruleCache.getSubPolicyUuidByRuleUuid(uuid), ruleDTO);

            }
            Long timestamp = eventDO.getGmtModify().getTime();
            SubPolicy oldSubPolicy = subPolicyCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if (timestamp != null && oldSubPolicy != null && timestampCompare(oldSubPolicy.getModifiedVersion(), timestamp) >= 0) {
                logger.debug(TraceUtils.getFormatTrace() + "SubPolicy reload localCache is newest, ignore uuid:{}", uuid);
                return true;
            }

            reloadByUuid(uuid);
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "SubPolicy reload failed, uuid:{}", uuid, e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace() + "SubPolicy reload success, uuid:{}", uuid);
        return true;
    }


    /**
     * 更新事件类型
     *
     * @return
     */
    public boolean addOrUpdate(SubPolicyEventDO eventDO) {
        String uuid = eventDO.getUuid();
        logger.debug(TraceUtils.getFormatTrace() + "SubPolicy reload start, uuid:{}", uuid);
        try {
            // 这里的RuleDTO都是开启的
            List<RuleDTO> ruleDOList = ruleRepository.queryFullBySubPolicyUuid(uuid);
            for (RuleDTO ruleDTO : ruleDOList) {
                Rule newRule = ruleConvertor.convert(ruleDTO);
                ruleCache.put(ruleDTO.getUuid(), newRule);
                //处理需要批量远程调用的数据
                this.addBatchRemoteCallDataToCache(ruleDTO.getPolicyUuid(), ruleCache.getSubPolicyUuidByRuleUuid(uuid), ruleDTO);

            }
            Long timestamp = eventDO.getGmtModify().getTime();
            SubPolicy oldSubPolicy = subPolicyCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if (timestamp != null && oldSubPolicy != null && timestampCompare(oldSubPolicy.getModifiedVersion(), timestamp) >= 0) {
                logger.debug(TraceUtils.getFormatTrace() + "SubPolicy reload localCache is newest, ignore uuid:{}", uuid);
                return true;
            }

            reloadByUuid(uuid);
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "SubPolicy reload failed, uuid:{}", uuid, e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace() + "SubPolicy reload success, uuid:{}", uuid);
        return true;
    }

    public void reloadByUuid(String subPolicyUuid) {
        SubPolicyDTO subPolicyDTO = subPolicyRepository.queryFullByUuid(subPolicyUuid);

        //如果失效则删除缓存 及 该子策略下相关规则的批量远程调用数据
        if (subPolicyDTO == null || !subPolicyDTO.isValid()) {
            removeSubPolicy(subPolicyUuid);
            return;
        }

        SubPolicy subPolicy = subPolicyConvertor.convert(subPolicyDTO);
        subPolicyCache.put(subPolicyUuid, subPolicy);

        //新增/更新该子策略下相关规则的批量远程调用数据
        this.addBatchRemoteCallDataToCache(subPolicy.getPolicyUuid(), subPolicyUuid, subPolicyDTO);
        //刷新引用到的平台指标
        policyIndicatrixItemReloadManager.reload(subPolicyDTO.getPolicyUuid());
        policyFieldReloadManager.reload(subPolicyDTO.getPolicyUuid());

    }


    /**
     * 删除事件类型
     *
     * @param eventDO
     * @return
     */
    @Override
    public boolean remove(SubPolicyEventDO eventDO) {
        try {
            boolean result = removeSubPolicy(eventDO.getUuid());
            logger.debug(TraceUtils.getFormatTrace() + "SubPolicy remove success, uuid:{}", eventDO.getUuid());
            return result;
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "SubPolicy remove failed, uuid:{}", eventDO.getUuid(), e);
            return false;
        }
    }

    /**
     * 关闭状态
     *
     * @param eventDO
     * @return
     */
    @Override
    public boolean deactivate(SubPolicyEventDO eventDO) {
        return remove(eventDO);
    }

    public boolean removeSubPolicy(String subPolicyUuid) {
        SubPolicy subPolicy = subPolicyCache.remove(subPolicyUuid);
        if (subPolicy == null) {
            return true;
        }

        List<Rule> ruleList = ruleCache.getRuleBySubPolicyUuid(subPolicy.getUuid());
        if (ruleList == null) {
            return true;
        }
        for (Rule rule : ruleList) {
            //删除规则
            ruleCache.remove(rule.getUuid());
            if (BatchRemoteCallDataBuilderFactory.supportBatchRemoteCall(rule.getTemplate())) {
                //删除该规则批量远程调用数据
                batchRemoteCallDataCache.remove(subPolicy.getPolicyUuid(), rule.getTemplate(), rule.getUuid());
            }
        }
        return true;
    }


    /**
     * 子策略排序，对当前执行暂无影响，不处理
     *
     * @param list
     * @return
     */
    @Override
    public boolean sort(List<SubPolicyEventDO> list) {
        return true;
    }

    /**
     * 添加/更新批量调用数据到cache
     *
     * @param policyUuid
     * @param subPolicyUuid
     * @param subPolicyDTO
     */
    private void addBatchRemoteCallDataToCache(String policyUuid, String subPolicyUuid, SubPolicyDTO subPolicyDTO) {
        List<RuleDTO> ruleDTOS = subPolicyDTO.getRules();
        if (!CollectionUtils.isEmpty(ruleDTOS)) {
            for (RuleDTO ruleDTO : ruleDTOS) {
                Map<String, List<Object>> batchdatas = BatchRemoteCallDataManager.buildData(policyUuid, subPolicyUuid, ruleDTO);
                if (!CollectionUtils.isEmpty(batchdatas)) {
                    Set<Map.Entry<String, List<Object>>> entries = batchdatas.entrySet();
                    for (Map.Entry<String, List<Object>> entry : entries) {
                        batchRemoteCallDataCache.addOrUpdate(policyUuid, entry.getKey(), ruleDTO.getUuid(), entry.getValue());
                    }
                }
            }
        }
    }

    /**
     * 添加/更新批量调用数据到cache
     *
     * @param policyUuid
     * @param subPolicyUuid
     * @param ruleDTO
     */
    private void addBatchRemoteCallDataToCache(String policyUuid, String subPolicyUuid, RuleDTO ruleDTO) {
        Map<String, List<Object>> batchdatas = BatchRemoteCallDataManager.buildData(policyUuid, subPolicyUuid, ruleDTO);
        if (!CollectionUtils.isEmpty(batchdatas)) {
            Set<Map.Entry<String, List<Object>>> entries = batchdatas.entrySet();
            for (Map.Entry<String, List<Object>> entry : entries) {
                batchRemoteCallDataCache.addOrUpdate(policyUuid, entry.getKey(), ruleDTO.getUuid(), entry.getValue());
            }
        }
    }

}
