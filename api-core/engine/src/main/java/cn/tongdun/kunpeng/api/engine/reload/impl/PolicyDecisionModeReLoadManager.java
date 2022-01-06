package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.constant.ReloadConstant;
import cn.tongdun.kunpeng.api.engine.convertor.impl.DecisionFlowConvertor;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.PolicyDecisionModeEventDO;
import cn.tongdun.kunpeng.client.dto.DecisionFlowDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDecisionModeDTO;
import cn.tongdun.kunpeng.api.engine.model.decisionflow.IDecisionFlowRepository;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.*;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
public class PolicyDecisionModeReLoadManager implements IReload<PolicyDecisionModeEventDO> {

    private Logger logger = LoggerFactory.getLogger(PolicyDecisionModeReLoadManager.class);

    @Autowired
    private IPolicyDecisionModeRepository policyDecisionModeRepository;

    @Autowired
    private IDecisionFlowRepository decisionFlowRepository;

    @Autowired
    private DecisionModeCache decisionModeCache;

    @Autowired
    private DecisionFlowConvertor decisionFlowConvertor;

    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init(){
        reloadFactory.register(PolicyDecisionModeEventDO.class,this);
    }

    @Override
    public boolean create(PolicyDecisionModeEventDO policyDecisionModeDO){
        return addOrUpdate(policyDecisionModeDO);
    }
    @Override
    public boolean update(PolicyDecisionModeEventDO policyDecisionModeDO){
        return addOrUpdate(policyDecisionModeDO);
    }
    @Override
    public boolean activate(PolicyDecisionModeEventDO policyDecisionModeDO){
        return addOrUpdate(policyDecisionModeDO);
    }

    /**
     * 更新事件类型
     * @return
     */
    public boolean addOrUpdate(PolicyDecisionModeEventDO policyDecisionModeDO){
        String policyUuid = policyDecisionModeDO.getPolicyUuid();
        logger.debug(TraceUtils.getFormatTrace()+"PolicyDecisionMode reload start, policyUuid:{}",policyUuid);
        try {
            Long timestamp = policyDecisionModeDO.getModifiedVersion();
            switchDecisionMode(policyUuid,timestamp);
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"PolicyDecisionMode reload failed, policyUuid:{}",policyUuid,e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace()+"PolicyDecisionMode reload success, policyUuid:{}",policyUuid);
        return true;
    }


    public boolean switchDecisionMode(String policyUuid,Long timestamp){
        try {
            AbstractDecisionMode decisionMode = decisionModeCache.get(policyUuid);

            //设置要查询的时间戳，如果redis缓存的时间戳比这新，则直接按redis缓存的数据返回
            ThreadContext.getContext().setAttr(ReloadConstant.THREAD_CONTEXT_ATTR_MODIFIED_VERSION,timestamp);
            PolicyDecisionModeDTO policyDecisionModeDTO = policyDecisionModeRepository.queryByPolicyUuid(policyUuid);
            ThreadContext.getContext().setAttr(ReloadConstant.THREAD_CONTEXT_ATTR_MODIFIED_VERSION,null);

            if(policyDecisionModeDTO == null || !policyDecisionModeDTO.isValid()){
                decisionModeCache.remove(policyUuid);
                return true;
            }
            //当前策略是否按决策流执行
            if(DecisionModeType.FLOW.name().equalsIgnoreCase(policyDecisionModeDTO.getDecisionModeType())){
                if(decisionMode instanceof DecisionFlow){
                    //缓存中的数据是相同版本或更新的，则不刷新
                    if(timestampCompare(decisionMode.getModifiedVersion(), timestamp) > 0){
                        logger.debug(TraceUtils.getFormatTrace()+"PolicyDecisionMode reload localCache is newest, ignore policyUuid:{}",policyUuid);
                        return true;
                    }
                }
            } else {
                if(decisionMode instanceof ParallelSubPolicy){
                    //缓存中的数据是相同版本或更新的，则不刷新
                    if(timestampCompare(decisionMode.getModifiedVersion(), timestamp) > 0){
                        logger.debug(TraceUtils.getFormatTrace()+"PolicyDecisionMode reload localCache is newest, ignore policyUuid:{}",policyUuid);
                        return true;
                    }
                }
            }

            //决策流
            if(DecisionModeType.FLOW.name().equalsIgnoreCase(policyDecisionModeDTO.getDecisionModeType())){
                DecisionFlowDTO decisionFlowDTO = decisionFlowRepository.queryByUuid(policyDecisionModeDTO.getDecisionModeUuid());
                DecisionFlow decisionFlow = decisionFlowConvertor.convert(decisionFlowDTO);
                decisionModeCache.put(decisionFlowDTO.getUuid(),decisionFlow);
            } else {
                //并行子策略
                ParallelSubPolicy parallelSubPolicy = new ParallelSubPolicy();
                parallelSubPolicy.setPolicyUuid(policyUuid);
                parallelSubPolicy.setGmtModify(policyDecisionModeDTO.getGmtModify());
                decisionModeCache.put(policyUuid,parallelSubPolicy);
            }
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"PolicyDecisionMode reload failed, policyUuid:{}",policyUuid,e);
            return false;
        }
        return true;
    }


    /**
     * 删除事件类型
     * @param policyDecisionModeDO
     * @return
     */
    @Override
    public boolean remove(PolicyDecisionModeEventDO policyDecisionModeDO){
        try {
            decisionModeCache.remove(policyDecisionModeDO.getPolicyUuid());
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"PolicyDecisionMode remove failed, uuid:{}",policyDecisionModeDO.getUuid(),e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace()+"PolicyDecisionMode remove success, uuid:{}",policyDecisionModeDO.getPolicyUuid());
        return true;
    }

    /**
     * 关闭状态
     * @param policyDecisionModeDO
     * @return
     */
    @Override
    public boolean deactivate(PolicyDecisionModeEventDO policyDecisionModeDO){
        return remove(policyDecisionModeDO);
    }

}
