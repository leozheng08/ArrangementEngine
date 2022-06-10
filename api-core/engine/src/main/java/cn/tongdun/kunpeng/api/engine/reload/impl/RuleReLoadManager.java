package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.cache.BatchRemoteCallDataCache;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilderFactory;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataManager;
import cn.tongdun.kunpeng.api.engine.convertor.impl.RuleConvertor;
import cn.tongdun.kunpeng.api.engine.model.constant.BizTypeEnum;
import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.rule.IRuleRepository;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.RuleEventDO;
import cn.tongdun.kunpeng.client.dto.RuleDTO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.google.common.collect.HashMultimap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
public class RuleReLoadManager implements IReload<RuleEventDO> {

    private Logger logger = LoggerFactory.getLogger(RuleReLoadManager.class);

    @Autowired
    private IRuleRepository ruleRepository;

    @Autowired
    private SubPolicyReLoadManager subPolicyReLoadManager;

    @Autowired
    private RuleCache ruleCache;

    @Autowired
    private ReloadFactory reloadFactory;

    @Autowired
    private RuleConvertor ruleConvertor;

    @Autowired
    private BatchRemoteCallDataCache batchRemoteCallDataCache;

    @Autowired
    private SubPolicyCache subPolicyCache;

    @PostConstruct
    public void init() {
        reloadFactory.register(RuleEventDO.class, this);
    }

    @Override
    public boolean create(RuleEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean update(RuleEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean activate(RuleEventDO eventDO) {
        return addOrUpdate(eventDO);
    }


    @Override
    public boolean remove(List<RuleEventDO> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        if (list.size() == 1) {
            return remove(list.get(0));
        }
        return batchAddOrUpdate(list);
    }

    ;

    @Override
    public boolean create(List<RuleEventDO> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        if (list.size() == 1) {
            return create(list.get(0));
        }
        return batchAddOrUpdate(list);
    }

    ;

    @Override
    public boolean update(List<RuleEventDO> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        if (list.size() == 1) {
            return update(list.get(0));
        }
        return batchAddOrUpdate(list);
    }

    ;

    @Override
    public boolean activate(List<RuleEventDO> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        if (list.size() == 1) {
            return activate(list.get(0));
        }
        return batchAddOrUpdate(list);
    }

    ;

    @Override
    public boolean deactivate(List<RuleEventDO> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        if (list.size() == 1) {
            return deactivate(list.get(0));
        }
        return batchAddOrUpdate(list);
    }

    ;

    /**
     * 排序只需要更新子策略中规则的顺序。
     *
     * @param eventDOlist
     * @return
     */
    @Override
    public boolean sort(List<RuleEventDO> eventDOlist) {
        try {
            if (eventDOlist == null || eventDOlist.isEmpty()) {
                return true;
            }

            List<String> ruleUuids = new ArrayList<>();
            for (RuleEventDO eventDO : eventDOlist) {
                ruleUuids.add(eventDO.getUuid());
            }

            List<RuleDTO> ruleDTOList = ruleRepository.queryByUuids(ruleUuids);

            //bizType -> Set<bizUuid>
            HashMultimap<String, String> hashMultimap = HashMultimap.create();
            for (RuleDTO ruleDTO : ruleDTOList) {
                hashMultimap.put(ruleDTO.getBizType(), ruleDTO.getBizUuid());
            }

            for (String bizType : hashMultimap.keySet()) {
                if (BizTypeEnum.SUB_POLICY.name().toLowerCase().equalsIgnoreCase(bizType)) {
                    Set<String> subPolicyUuids = hashMultimap.get(bizType);

                    if (subPolicyUuids == null || subPolicyUuids.isEmpty()) {
                        continue;
                    }

                    for (String subPolicyUuid : subPolicyUuids) {
                        subPolicyReLoadManager.reloadByUuid(subPolicyUuid);
                    }
                }
                //其他bizType待后期扩展
            }
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "Rule batchAddOrUpdate failed", e);
            return false;
        }
        return true;
    }

    ;


    /**
     * 更新事件类型
     *
     * @return
     */
    public boolean addOrUpdate(RuleEventDO eventDO) {
        String uuid = eventDO.getUuid();
        logger.debug(TraceUtils.getFormatTrace() + "Rule reload start, uuid:{}, currentTime={}", uuid, System.currentTimeMillis());
        try {
            Long timestamp = eventDO.getGmtModify().getTime();
            Rule oldRule = ruleCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if (timestamp != null && oldRule != null && timestampCompare(oldRule.getModifiedVersion(), timestamp) >= 0) {
                logger.debug(TraceUtils.getFormatTrace() + "Rule reload localCache is newest, ignore uuid:{}", uuid);
                return true;
            }

            RuleDTO ruleDTO = ruleRepository.queryFullByUuid(uuid);

            //如果失效则删除缓存
            if (ruleDTO == null || !ruleDTO.isValid()) {
                return remove(eventDO);
            }

            Rule newRule = ruleConvertor.convert(ruleDTO);
            ruleCache.put(uuid, newRule);

            //处理需要批量远程调用的数据
            this.addBatchRemoteCallDataToCache(ruleDTO.getPolicyUuid(), ruleCache.getSubPolicyUuidByRuleUuid(uuid), ruleDTO);

            //刷新子策略下规则的执行顺序
            subPolicyReLoadManager.reloadByUuid(ruleDTO.getBizUuid());
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "Rule reload failed, uuid:{}", uuid, e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace() + "Rule reload success, uuid:{}, currentTime={}", uuid, System.currentTimeMillis());
        return true;
    }

    /**
     * 批量变更时，则整个子策略重新加载
     *
     * @param eventDOlist
     * @return
     */
    public boolean batchAddOrUpdate(List<RuleEventDO> eventDOlist) {
        try {
            if (eventDOlist == null || eventDOlist.isEmpty()) {
                return true;
            }
            //bizType -> Set<bizUuid>
            HashMultimap<String, String> hashMultimap = HashMultimap.create();

            for (RuleEventDO eventDO : eventDOlist) {
                String uuid = eventDO.getUuid();
                Long timestamp = eventDO.getGmtModify().getTime();
                Rule oldRule = ruleCache.get(uuid);
                //缓存中的数据是相同版本或更新的，则不刷新
                if (timestamp != null && oldRule != null && timestampCompare(oldRule.getModifiedVersion(), timestamp) >= 0) {
                    logger.debug(TraceUtils.getFormatTrace() + "Rule reload localCache is newest, ignore uuid:{}", uuid);
                    return true;
                }

                RuleDTO ruleDTO = ruleRepository.queryFullByUuid(uuid);

                //如果失效则删除缓存
                if (ruleDTO == null || CommonStatusEnum.CLOSE.getCode() == ruleDTO.getStatus()) {
                    return remove(eventDO);
                }

                Rule newRule = ruleConvertor.convert(ruleDTO);
                ruleCache.put(uuid, newRule);
                hashMultimap.put(ruleDTO.getBizType(), ruleDTO.getBizUuid());

                //处理需要批量远程调用的数据
                this.addBatchRemoteCallDataToCache(ruleDTO.getPolicyUuid(), ruleCache.getSubPolicyUuidByRuleUuid(uuid), ruleDTO);
            }

            for (String bizType : hashMultimap.keySet()) {
                if (BizTypeEnum.SUB_POLICY.name().toLowerCase().equalsIgnoreCase(bizType)) {
                    Set<String> subPolicyUuids = hashMultimap.get(bizType);

                    if (subPolicyUuids == null || subPolicyUuids.isEmpty()) {
                        continue;
                    }

                    for (String subPolicyUuid : subPolicyUuids) {
                        subPolicyReLoadManager.reloadByUuid(subPolicyUuid);
                    }
                }
                //其他bizType待后期扩展
            }
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "Rule batchAddOrUpdate failed", e);
            return false;
        }
        return true;
    }


    /**
     * 删除事件类型
     *
     * @param eventDO
     * @return
     */
    @Override
    public boolean remove(RuleEventDO eventDO) {
        try {
            //移除远程批量调用相关缓存   在最前面移除，如果其他缓存先移除，就无法从缓存中获取信息了
            Rule cacheRule = ruleCache.get(eventDO.getUuid());
            if (null != cacheRule) {
                if (BatchRemoteCallDataBuilderFactory.supportBatchRemoteCall(cacheRule.getTemplate())) {
                    String subPolicyUuid = ruleCache.getSubPolicyUuidByRuleUuid(eventDO.getUuid());
                    String policyUuid = subPolicyCache.getPolicyUuidBySubPolicyUuid(subPolicyUuid);
                    if (cacheRule != null && StringUtils.isNotBlank(subPolicyUuid) && StringUtils.isNotBlank(policyUuid)) {
                        batchRemoteCallDataCache.remove(policyUuid, cacheRule.getTemplate(), eventDO.getUuid());
                    } else {
                        //理论上，子策略都删除了，应该不会发生规则的删除事件了
                        //同理，如果策略都删除了，应该不会发生子策略的删除事件了
                        //所以如果此方法代码被调用，但是上面的subPolicyUuid，policyUuid却为null，则需要排查原因
                        logger.error("防御性日志，如果大量出现，需要检查代码：subPolicyUuid = {},policyUuid = {}", subPolicyUuid, policyUuid);
                    }
                }
            }


            //刷新子策略下规则的执行顺序
            subPolicyReLoadManager.reloadByUuid(cacheRule.getBizUuid());

            // 规则缓存后续删除，保证子策略规则的缓存大于规则缓存
            Rule rule = ruleCache.remove(eventDO.getUuid());
            if (rule == null) {
                return true;
            }
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "Rule remove failed, uuid:{}", eventDO.getUuid(), e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace() + "Rule remove success, uuid:{}", eventDO.getUuid());
        return true;
    }

    /**
     * 关闭状态
     *
     * @param eventDO
     * @return
     */
    @Override
    public boolean deactivate(RuleEventDO eventDO) {
        return remove(eventDO);
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
