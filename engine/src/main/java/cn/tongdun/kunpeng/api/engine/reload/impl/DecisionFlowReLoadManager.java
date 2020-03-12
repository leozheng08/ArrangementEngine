package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.DecisionFlowConvertor;
import cn.tongdun.kunpeng.api.engine.dto.DecisionFlowDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDecisionModeDTO;
import cn.tongdun.kunpeng.api.engine.model.decisionflow.IDecisionFlowRepository;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.*;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.DecisionFlowDO;
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
public class DecisionFlowReLoadManager implements IReload<DecisionFlowDO> {

    private Logger logger = LoggerFactory.getLogger(DecisionFlowReLoadManager.class);

    @Autowired
    private IDecisionFlowRepository decisionFlowRepository;

    private IPolicyDecisionModeRepository policyDecisionModeRepository;

    @Autowired
    private DecisionModeCache decisionModeCache;

    @Autowired
    private DecisionFlowConvertor decisionFlowConvertor;

    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init(){
        reloadFactory.register(DecisionFlowDO.class,this);
    }

    /**
     * 更新事件类型
     * @return
     */
    @Override
    public boolean addOrUpdate(DecisionFlowDO decisionFlowDO){
        String uuid = decisionFlowDO.getUuid();
        logger.debug("DecisionFlowReLoadManager start, uuid:{}",uuid);
        try {
            PolicyDecisionModeDTO policyDecisionModeDTO = policyDecisionModeRepository.queryByPolicyUuid(decisionFlowDO.getUuid());
            if(policyDecisionModeDTO == null){
                return true;
            }
            //当前策略是否按决策流执行
            if(!DecisionModeType.FLOW.name().equalsIgnoreCase(policyDecisionModeDTO.getDecisionModeType())){
                return true;
            }

            Long timestamp = decisionFlowDO.getGmtModify().getTime();
            AbstractDecisionMode decisionMode = decisionModeCache.get(uuid);
            if(decisionMode instanceof DecisionFlow){
                //缓存中的数据是相同版本或更新的，则不刷新
                if(decisionMode.getModifiedVersion()  >= timestamp){
                    return true;
                }
            }


            DecisionFlowDTO decisionFlowDTO = decisionFlowRepository.queryByUuid(uuid);
            if(decisionFlowDTO == null){
                return true;
            }
            DecisionFlow decisionFlow = decisionFlowConvertor.convert(decisionFlowDTO);
            decisionModeCache.put(uuid,decisionFlow);
        } catch (Exception e){
            logger.error("DecisionFlowReLoadManager failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug("DecisionFlowReLoadManager success, uuid:{}",uuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param decisionFlowDO
     * @return
     */
    @Override
    public boolean remove(DecisionFlowDO decisionFlowDO){
        try {
            decisionModeCache.remove(decisionFlowDO.getUuid());
        } catch (Exception e){
            return false;
        }
        return true;
    }
}
