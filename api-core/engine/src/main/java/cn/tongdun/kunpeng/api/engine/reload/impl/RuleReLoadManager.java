package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.cache.BatchRemoteCallDataCache;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataManager;
import cn.tongdun.kunpeng.api.engine.convertor.impl.RuleConvertor;
import cn.tongdun.kunpeng.api.engine.model.constant.BizTypeEnum;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.RuleEventDO;
import cn.tongdun.kunpeng.client.dto.RuleDTO;
import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.rule.IRuleRepository;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.google.common.collect.HashMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
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
    private PolicyIndicatrixItemReloadManager policyIndicatrixItemReloadManager;

    @Autowired
    private RuleCache ruleCache;

    @Autowired
    private ReloadFactory reloadFactory;

    @Autowired
    private RuleConvertor ruleConvertor;

    @Autowired
    private BatchRemoteCallDataCache batchRemoteCallDataCache;


    @PostConstruct
    public void init(){
        reloadFactory.register(RuleEventDO.class,this);
    }

    @Override
    public boolean create(RuleEventDO eventDO){
        return addOrUpdate(eventDO);
    }
    @Override
    public boolean update(RuleEventDO eventDO){
        return addOrUpdate(eventDO);
    }
    @Override
    public boolean activate(RuleEventDO eventDO){
        return addOrUpdate(eventDO);
    }


    @Override
    public boolean remove(List<RuleEventDO> list) {
        if(list == null || list.isEmpty()){
            return true;
        }
        if(list.size() == 1){
            remove(list.get(0));
        }
        return batchAddOrUpdate(list);
    };
    @Override
    public boolean create(List<RuleEventDO> list){
        if(list == null || list.isEmpty()){
            return true;
        }
        if(list.size() == 1){
            create(list.get(0));
        }
        return batchAddOrUpdate(list);
    };
    @Override
    public boolean update(List<RuleEventDO> list) {
        if(list == null || list.isEmpty()){
            return true;
        }
        if(list.size() == 1){
            update(list.get(0));
        }
        return batchAddOrUpdate(list);
    };
    @Override
    public boolean activate(List<RuleEventDO> list) {
        if(list == null || list.isEmpty()){
            return true;
        }
        if(list.size() == 1){
            activate(list.get(0));
        }
        return batchAddOrUpdate(list);
    };
    @Override
    public boolean deactivate(List<RuleEventDO> list) {
        if(list == null || list.isEmpty()){
            return true;
        }
        if(list.size() == 1){
            deactivate(list.get(0));
        }
        return batchAddOrUpdate(list);
    };

    /**
     * 排序只需要更新子策略中规则的顺序。
     * @param eventDOlist
     * @return
     */
    @Override
    public boolean sort(List<RuleEventDO> eventDOlist) {
        try {
            if(eventDOlist == null || eventDOlist.isEmpty()){
                return true;
            }

            List<String> ruleUuids = new ArrayList<>();
            for(RuleEventDO eventDO : eventDOlist) {
                ruleUuids.add(eventDO.getUuid());
            }

            List<RuleDTO> ruleDTOList = ruleRepository.queryByUuids(ruleUuids);

            //bizType -> Set<bizUuid>
            HashMultimap<String,String> hashMultimap = HashMultimap.create();
            for(RuleDTO ruleDTO : ruleDTOList) {
                hashMultimap.put(ruleDTO.getBizType(),ruleDTO.getBizUuid());
            }

            for(String bizType : hashMultimap.keySet()){
                if(BizTypeEnum.SUB_POLICY.name().toLowerCase().equalsIgnoreCase(bizType)){
                    Set<String> subPolicyUuids = hashMultimap.get(bizType);

                    if(subPolicyUuids == null || subPolicyUuids.isEmpty()){
                        continue;
                    }

                    for(String subPolicyUuid : subPolicyUuids) {
                        subPolicyReLoadManager.reloadByUuid(subPolicyUuid);
                    }
                }
                //其他bizType待后期扩展
            }
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"Rule batchAddOrUpdate failed",e);
            return false;
        }
        return true;
    };



    /**
     * 更新事件类型
     * @return
     */
    public boolean addOrUpdate(RuleEventDO eventDO){
        String uuid = eventDO.getUuid();
        logger.debug(TraceUtils.getFormatTrace()+"Rule reload start, uuid:{}",uuid);
        try {
            Long timestamp = eventDO.getGmtModify().getTime();
            Rule oldRule = ruleCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && oldRule != null && timestampCompare(oldRule.getModifiedVersion(), timestamp) >= 0) {
                logger.debug(TraceUtils.getFormatTrace()+"Rule reload localCache is newest, ignore uuid:{}",uuid);
                return true;
            }

            RuleDTO ruleDTO = ruleRepository.queryFullByUuid(uuid);

            //如果失效则删除缓存
            if(ruleDTO == null || !ruleDTO.isValid()){
                return remove(eventDO);
            }

            Rule newRule = ruleConvertor.convert(ruleDTO);
            ruleCache.put(uuid,newRule);

            //处理需要批量远程调用的数据
            String subPolicyUuid = ruleCache.getSubPolicyUuidByRuleUuid(uuid);
            List<Object> batchRemoteCallDatas = BatchRemoteCallDataManager.buildData(ruleDTO.getPolicyUuid(),subPolicyUuid,ruleDTO);
            batchRemoteCallDataCache.addOrUpdate(ruleDTO.getPolicyUuid(),ruleDTO.getTemplate(),uuid,batchRemoteCallDatas);

            //刷新子策略下规则的执行顺序
            subPolicyReLoadManager.reloadByUuid(ruleDTO.getBizUuid());
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"Rule reload failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace()+"Rule reload success, uuid:{}",uuid);
        return true;
    }

    /**
     * 批量变更时，则整个子策略重新加载
     * @param eventDOlist
     * @return
     */
    public boolean batchAddOrUpdate(List<RuleEventDO> eventDOlist){
        try {
            if(eventDOlist == null || eventDOlist.isEmpty()){
                return true;
            }
            //bizType -> Set<bizUuid>
            HashMultimap<String,String> hashMultimap = HashMultimap.create();

            for(RuleEventDO eventDO : eventDOlist) {
                String uuid = eventDO.getUuid();
                Long timestamp = eventDO.getGmtModify().getTime();
                Rule oldRule = ruleCache.get(uuid);
                //缓存中的数据是相同版本或更新的，则不刷新
                if(timestamp != null && oldRule != null && timestampCompare(oldRule.getModifiedVersion(), timestamp) >= 0) {
                    logger.debug(TraceUtils.getFormatTrace()+"Rule reload localCache is newest, ignore uuid:{}",uuid);
                    return true;
                }

                RuleDTO ruleDTO = ruleRepository.queryFullByUuid(uuid);

                //如果失效则删除缓存
                if(ruleDTO == null || CommonStatusEnum.CLOSE.getCode() == ruleDTO.getStatus()){
                    return remove(eventDO);
                }

                Rule newRule = ruleConvertor.convert(ruleDTO);
                ruleCache.put(uuid,newRule);
                hashMultimap.put(ruleDTO.getBizType(),ruleDTO.getBizUuid());

                //处理需要批量远程调用的数据
                String subPolicyUuid = ruleCache.getSubPolicyUuidByRuleUuid(uuid);
                List<Object> batchRemoteCallDatas = BatchRemoteCallDataManager.buildData(ruleDTO.getPolicyUuid(),subPolicyUuid,ruleDTO);
                batchRemoteCallDataCache.addOrUpdate(ruleDTO.getPolicyUuid(),ruleDTO.getTemplate(),uuid,batchRemoteCallDatas);
            }

            for(String bizType : hashMultimap.keySet()){
                if(BizTypeEnum.SUB_POLICY.name().toLowerCase().equalsIgnoreCase(bizType)){
                    Set<String> subPolicyUuids = hashMultimap.get(bizType);

                    if(subPolicyUuids == null || subPolicyUuids.isEmpty()){
                        continue;
                    }

                    for(String subPolicyUuid : subPolicyUuids) {
                        subPolicyReLoadManager.reloadByUuid(subPolicyUuid);
                    }
                }
                //其他bizType待后期扩展
            }
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"Rule batchAddOrUpdate failed",e);
            return false;
        }
        return true;
    }


    /**
     * 删除事件类型
     * @param eventDO
     * @return
     */
    @Override
    public boolean remove(RuleEventDO eventDO){
        try {
            Rule rule = ruleCache.remove(eventDO.getUuid());
            if(rule == null){
                return true;
            }
            //刷新子策略下规则的执行顺序
            subPolicyReLoadManager.reloadByUuid(rule.getBizUuid());

            //移除远程批量调用相关缓存
            RuleDTO ruleDTO = ruleRepository.queryFullByUuid(eventDO.getUuid());//TODO 优化只查询需要的属性，实体太大
            String policyUuid = ruleDTO.getPolicyUuid();
            batchRemoteCallDataCache.remove(policyUuid, ruleDTO.getTemplate(), eventDO.getUuid());
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"Rule remove failed, uuid:{}",eventDO.getUuid(),e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace()+"Rule remove success, uuid:{}",eventDO.getUuid());
        return true;
    }

    /**
     * 关闭状态
     * @param eventDO
     * @return
     */
    @Override
    public boolean deactivate(RuleEventDO eventDO){
        return remove(eventDO);
    }




}
