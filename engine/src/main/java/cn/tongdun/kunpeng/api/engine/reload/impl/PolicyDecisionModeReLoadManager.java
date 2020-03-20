package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.DecisionFlowConvertor;
import cn.tongdun.kunpeng.client.dto.DecisionFlowDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDecisionModeDTO;
import cn.tongdun.kunpeng.api.engine.model.decisionflow.IDecisionFlowRepository;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.*;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.IndexDefinitionDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDecisionModeDO;
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
public class PolicyDecisionModeReLoadManager implements IReload<PolicyDecisionModeDO> {

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
        reloadFactory.register(PolicyDecisionModeDO.class,this);
    }

    @Override
    public boolean create(PolicyDecisionModeDO policyDecisionModeDO){
        return addOrUpdate(policyDecisionModeDO);
    }
    @Override
    public boolean update(PolicyDecisionModeDO policyDecisionModeDO){
        return addOrUpdate(policyDecisionModeDO);
    }
    @Override
    public boolean activate(PolicyDecisionModeDO policyDecisionModeDO){
        return addOrUpdate(policyDecisionModeDO);
    }

    /**
     * 更新事件类型
     * @return
     */
    public boolean addOrUpdate(PolicyDecisionModeDO policyDecisionModeDO){
        String policyUuid = policyDecisionModeDO.getPolicyUuid();
        logger.debug("PolicyDecisionMode reload start, policyUuid:{}",policyUuid);
        try {
            Long timestamp = policyDecisionModeDO.getGmtModify().getTime();
            AbstractDecisionMode decisionMode = decisionModeCache.get(policyUuid);

            PolicyDecisionModeDTO policyDecisionModeDTO = policyDecisionModeRepository.queryByPolicyUuid(policyUuid);
            if(policyDecisionModeDTO == null){
                return remove(policyDecisionModeDO);
            }
            //当前策略是否按决策流执行
            if(DecisionModeType.FLOW.name().equalsIgnoreCase(policyDecisionModeDTO.getDecisionModeType())){
                if(decisionMode instanceof DecisionFlow){
                    //缓存中的数据是相同版本或更新的，则不刷新
                    if(decisionMode.getModifiedVersion()  >= timestamp){
                        logger.debug("PolicyDecisionMode reload localCache is newest, ignore policyUuid:{}",policyUuid);
                        return true;
                    }
                }
            } else {
                if(decisionMode instanceof ParallelSubPolicy){
                    //缓存中的数据是相同版本或更新的，则不刷新
                    if(decisionMode.getModifiedVersion()  >= timestamp){
                        logger.debug("PolicyDecisionMode reload localCache is newest, ignore policyUuid:{}",policyUuid);
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
                decisionModeCache.put(policyUuid,parallelSubPolicy);
            }
        } catch (Exception e){
            logger.error("PolicyDecisionMode reload failed, policyUuid:{}",policyUuid,e);
            return false;
        }
        logger.debug("PolicyDecisionMode reload success, policyUuid:{}",policyUuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param policyDecisionModeDO
     * @return
     */
    @Override
    public boolean remove(PolicyDecisionModeDO policyDecisionModeDO){
        try {
            decisionModeCache.remove(policyDecisionModeDO.getPolicyUuid());
        } catch (Exception e){
            logger.error("PolicyDecisionMode remove failed, uuid:{}",policyDecisionModeDO.getUuid(),e);
            return false;
        }
        logger.debug("PolicyDecisionMode remove success, uuid:{}",policyDecisionModeDO.getPolicyUuid());
        return true;
    }

    /**
     * 关闭状态
     * @param policyDecisionModeDO
     * @return
     */
    @Override
    public boolean deactivate(PolicyDecisionModeDO policyDecisionModeDO){
        return remove(policyDecisionModeDO);
    }

}
