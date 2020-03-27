package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.DecisionFlowConvertor;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.DecisionFlowEventDO;
import cn.tongdun.kunpeng.client.dto.DecisionFlowDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDecisionModeDTO;
import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.decisionflow.IDecisionFlowRepository;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.*;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
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
public class DecisionFlowReLoadManager implements IReload<DecisionFlowEventDO> {

    private Logger logger = LoggerFactory.getLogger(DecisionFlowReLoadManager.class);

    @Autowired
    private IDecisionFlowRepository decisionFlowRepository;

    @Autowired
    private IPolicyDecisionModeRepository policyDecisionModeRepository;

    @Autowired
    private DecisionModeCache decisionModeCache;

    @Autowired
    private DecisionFlowConvertor decisionFlowConvertor;

    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init(){
        reloadFactory.register(DecisionFlowEventDO.class,this);
    }

    @Override
    public boolean create(DecisionFlowEventDO eventDO){
        return addOrUpdate(eventDO);
    }
    @Override
    public boolean update(DecisionFlowEventDO eventDO){
        return addOrUpdate(eventDO);
    }
    @Override
    public boolean activate(DecisionFlowEventDO eventDO){
        return addOrUpdate(eventDO);
    }

    /**
     * 更新事件类型
     * @return
     */
    public boolean addOrUpdate(DecisionFlowEventDO eventDO){
        String uuid = eventDO.getUuid();
        logger.debug("DecisionFlow reload start, uuid:{}",uuid);
        try {
            PolicyDecisionModeDTO policyDecisionModeDTO = policyDecisionModeRepository.queryByPolicyUuid(eventDO.getUuid());
            if(policyDecisionModeDTO == null){
                return true;
            }
            //当前策略是否按决策流执行
            if(!DecisionModeType.FLOW.name().equalsIgnoreCase(policyDecisionModeDTO.getDecisionModeType())){
                return true;
            }

            Long timestamp = eventDO.getModifiedVersion();
            AbstractDecisionMode decisionMode = decisionModeCache.get(uuid);
            if(decisionMode instanceof DecisionFlow){
                //缓存中的数据是相同版本或更新的，则不刷新
                if(decisionMode.getModifiedVersion()  >= timestamp){
                    logger.debug("DecisionFlow reload localCache is newest, ignore uuid:{}",uuid);
                    return true;
                }
            }


            DecisionFlowDTO decisionFlowDTO = decisionFlowRepository.queryByUuid(uuid);
            if(decisionFlowDTO == null){
                return true;
            }
            //如果失效则删除缓存
            if(decisionFlowDTO == null || !decisionFlowDTO.isValid()){
                return remove(eventDO);
            }

            DecisionFlow decisionFlow = decisionFlowConvertor.convert(decisionFlowDTO);
            decisionModeCache.put(uuid,decisionFlow);
        } catch (Exception e){
            logger.error("DecisionFlow reload failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug("DecisionFlow reload success, uuid:{}",uuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param eventDO
     * @return
     */
    @Override
    public boolean remove(DecisionFlowEventDO eventDO){
        try {
            decisionModeCache.remove(eventDO.getUuid());
        } catch (Exception e){
            logger.error("DecisionFlow remove failed, uuid:{}",eventDO.getUuid(),e);
            return false;
        }
        logger.debug("DecisionFlow remove success, uuid:{}",eventDO.getUuid());
        return true;
    }


    /**
     * 关闭状态
     * @param eventDO
     * @return
     */
    @Override
    public boolean deactivate(DecisionFlowEventDO eventDO){
        return remove(eventDO);
    }
}
