package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;


import cn.tongdun.kunpeng.client.dto.DecisionFlowDTO;
import cn.tongdun.kunpeng.api.engine.model.decisionflow.IDecisionFlowRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.DecisionFlowDAO;
import cn.tongdun.kunpeng.share.dataobject.DecisionFlowDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @Author: liang.chen
 * @Date: 2019/12/18 上午11:42
 */
@Repository
public class DecisionFlowRepository implements IDecisionFlowRepository{

    private Logger logger = LoggerFactory.getLogger(DecisionFlowRepository.class);

    @Autowired
    private DecisionFlowDAO decisionFlowDAO;


    /**
     * 查询决策流, 决策流的uuid与策略的uuid为值相同
     */
    @Override
    public DecisionFlowDTO queryByUuid(String policyUuid){
        DecisionFlowDO decisionFlowDO = decisionFlowDAO.selectByUuid(policyUuid);
        if(decisionFlowDO == null) {
            return null;
        }

        DecisionFlowDTO decisionFlowDTO = new DecisionFlowDTO();
        BeanUtils.copyProperties(decisionFlowDO,decisionFlowDTO);

        return decisionFlowDTO;
    }
}
