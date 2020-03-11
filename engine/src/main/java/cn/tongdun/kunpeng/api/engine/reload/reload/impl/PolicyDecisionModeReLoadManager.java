package cn.tongdun.kunpeng.api.engine.reload.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.DecisionFlowConvertor;
import cn.tongdun.kunpeng.api.engine.convertor.impl.SubPolicyConvertor;
import cn.tongdun.kunpeng.api.engine.dto.DecisionFlowDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDecisionModeDTO;
import cn.tongdun.kunpeng.api.engine.dto.SubPolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.decisionflow.IDecisionFlowRepository;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.*;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.IPolicyDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinitionCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.ISubPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import cn.tongdun.kunpeng.api.engine.reload.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.PolicyDecisionModeDO;
import cn.tongdun.kunpeng.share.dataobject.SubPolicyDO;
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

    /**
     * 更新事件类型
     * @return
     */
    @Override
    public boolean addOrUpdate(PolicyDecisionModeDO policyDecisionModeDO){
        String policyUuid = policyDecisionModeDO.getPolicyUuid();
        logger.debug("PolicyDecisionModeReLoadManager start, policyUuid:{}",policyUuid);
        try {
            Long timestamp = policyDecisionModeDO.getGmtModify().getTime();
            AbstractDecisionMode decisionMode = decisionModeCache.get(policyUuid);

            PolicyDecisionModeDTO policyDecisionModeDTO = policyDecisionModeRepository.queryByPolicyUuid(policyUuid);
            //当前策略是否按决策流执行
            if(DecisionModeType.FLOW.name().equalsIgnoreCase(policyDecisionModeDTO.getDecisionModeType())){
                if(decisionMode instanceof DecisionFlow){
                    //缓存中的数据是相同版本或更新的，则不刷新
                    if(decisionMode.getModifiedVersion()  >= timestamp){
                        return true;
                    }
                }
            } else {
                if(decisionMode instanceof ParallelSubPolicy){
                    //缓存中的数据是相同版本或更新的，则不刷新
                    if(decisionMode.getModifiedVersion()  >= timestamp){
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
            logger.error("PolicyDecisionModeReLoadManager failed, policyUuid:{}",policyUuid,e);
            return false;
        }
        logger.debug("PolicyDecisionModeReLoadManager success, policyUuid:{}",policyUuid);
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
            return false;
        }
        return true;
    }
}
