package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.cache.BatchRemoteCallDataCache;
import cn.tongdun.kunpeng.api.engine.constant.ReloadConstant;
import cn.tongdun.kunpeng.api.engine.convertor.impl.PolicyConvertor;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.constant.DeleteStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionModeCache;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndexCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.PolicyEventDO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.ThreadContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
public class PolicyReLoadManager implements IReload<PolicyEventDO> {

    private Logger logger = LoggerFactory.getLogger(PolicyReLoadManager.class);


    @Autowired
    private IPolicyRepository policyRepository;

    @Autowired
    private PolicyCache policyCache;

    @Autowired
    private PolicyConvertor policyConvertor;

    @Autowired
    private ReloadFactory reloadFactory;

    @Autowired
    private DecisionModeCache decisionModeCache;

    @Autowired
    private SubPolicyCache subPolicyCache;

    @Autowired
    private PolicyIndexCache policyIndexCache;

    @Autowired
    private SubPolicyReLoadManager subPolicyReLoadManager;

    @Autowired
    private PolicyDecisionModeReLoadManager policyDecisionModeReLoadManager;

    @Autowired
    private BatchRemoteCallDataCache batchRemoteCallDataCache;

    @PostConstruct
    public void init(){
        reloadFactory.register(PolicyEventDO.class,this);
    }

    @Override
    public boolean create(PolicyEventDO eventDO){
        return addOrUpdate(eventDO);
    }
    @Override
    public boolean update(PolicyEventDO eventDO){
        return addOrUpdate(eventDO);
    }
    @Override
    public boolean activate(PolicyEventDO eventDO){
        return addOrUpdate(eventDO);
    }


    /**
     * 更新事件类型
     * @return
     */
    public boolean addOrUpdate(PolicyEventDO eventDO){
        String uuid = eventDO.getUuid();
        logger.debug(TraceUtils.getFormatTrace()+"Policy reload start, uuid:{}",uuid);
        try {
            Long timestamp = eventDO.getModifiedVersion();
            Policy oldPolicy = policyCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && oldPolicy != null && timestampCompare(oldPolicy.getModifiedVersion(), timestamp) >= 0) {
                logger.debug(TraceUtils.getFormatTrace()+"Policy reload localCache is newest, ignore uuid:{}",uuid);
                return true;
            }

            //设置要查询的时间戳，如果redis缓存的时间戳比这新，则直接按redis缓存的数据返回
            ThreadContext.getContext().setAttr(ReloadConstant.THREAD_CONTEXT_ATTR_MODIFIED_VERSION,timestamp);
            PolicyDTO policyDTO = policyRepository.queryByUuid(uuid);
            //如果失效则删除缓存
            if(policyDTO == null || !policyDTO.isValid()){
                return remove(eventDO);
            }

            Policy policy = policyConvertor.convert(policyDTO);
            policyCache.put(uuid,policy);
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"Policy reload failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace()+"Policy reload success, uuid:{}",uuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param eventDO
     * @return
     */
    @Override
    public boolean remove(PolicyEventDO eventDO){
        try {
            String policyUuid = eventDO.getUuid();
            removePolicy(policyUuid);
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"Policy remove failed, uuid:{}",eventDO.getUuid(),e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace()+"Policy remove success, uuid:{}",eventDO.getUuid());
        return true;
    }

    /**
     * 关闭状态
     * @param eventDO
     * @return
     */
    @Override
    public boolean deactivate(PolicyEventDO eventDO){
        try {
            String policyUuid = eventDO.getUuid();
            Policy policy = policyCache.get(policyUuid);
            if(policy == null){
                return true;
            }

            //标记不在用状态
            policy.setStatus(CommonStatusEnum.CLOSE.getCode());

            //级联删除各个子对象
            cascadeRemove(policyUuid);
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"Policy deactivate failed, uuid:{}",eventDO.getUuid(),e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace()+"Policy deactivate success, uuid:{}",eventDO.getUuid());
        return true;
    }



    /**
     * 级联删除各个子对象
     * @param policyUuid
     * @return
     */
    public boolean removePolicy(String policyUuid){
        if(policyUuid == null){
            return true;
        }
        Policy policy = policyCache.remove(policyUuid);
        if(policy == null){
            return true;
        }

        //标记为删除状态
        policy.setDeleted(DeleteStatusEnum.INVALID.getCode());

        //级联删除各个子对象
        return cascadeRemove(policyUuid);
    }

    public boolean cascadeRemove(String policyUuid){

        if(StringUtils.isBlank(policyUuid)){
            return true;
        }

        //删除策略运行模式
        decisionModeCache.remove(policyUuid);

        policyIndexCache.removeList(policyUuid);

        //删除策略下相关规则的批量远程调用数据
        batchRemoteCallDataCache.remove(policyUuid);

        List<SubPolicy> subPolicyList = subPolicyCache.getSubPolicyByPolicyUuid(policyUuid);
        if(subPolicyList == null) {
            return true;
        }
        //删除子策略
        for(SubPolicy subPolicy:subPolicyList){
            subPolicyReLoadManager.removeSubPolicy(subPolicy.getUuid());
        }
        return true;
    }


    /**
     * 更改策略执行模式
     * @param eventDO
     * @return
     */
    @Override
    public boolean switchDecisionMode(PolicyEventDO eventDO){
        try {
            policyDecisionModeReLoadManager.switchDecisionMode(eventDO.getUuid(),eventDO.getGmtModify().getTime());
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"Policy switchDecisionMode failed, uuid:{}",eventDO.getUuid(),e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace()+"Policy switchDecisionMode success, uuid:{}",eventDO.getUuid());
        return true;
    }
}
