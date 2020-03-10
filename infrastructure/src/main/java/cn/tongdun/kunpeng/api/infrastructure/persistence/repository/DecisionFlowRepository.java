package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;


import cn.tongdun.kunpeng.api.engine.dto.DecisionFlowDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDecisionModeDTO;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.DecisionFlowDOMapper;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyDecisionModeDOMapper;
import cn.tongdun.kunpeng.share.dataobject.DecisionFlowDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDecisionModeDO;
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
public class PolicyDecisionModeRepository implements IPolicyRepository{

    private Logger logger = LoggerFactory.getLogger(PolicyDecisionModeRepository.class);

    @Autowired
    private PolicyDecisionModeDOMapper policyDecisionModeDOMapper;
    @Autowired
    private  DecisionFlowDOMapper decisionFlowDOMapper;




    //查询策略运行模式
    private PolicyDecisionModeDTO queryPolicyDecisionModeDTOByPolicyUuid(String policyUuid){
        PolicyDecisionModeDO policyDecisionModeDO = policyDecisionModeDOMapper.selectByPolicyUuid(policyUuid);
        if(policyDecisionModeDO == null) {
            return null;
        }

        PolicyDecisionModeDTO policyDecisionModeDTO = new PolicyDecisionModeDTO();
        BeanUtils.copyProperties(policyDecisionModeDO,policyDecisionModeDTO);

        return policyDecisionModeDTO;
    }

    //查询决策流
    private DecisionFlowDTO queryDecisionFlowDTOByPolicyUuid(String policyUuid){
        DecisionFlowDO decisionFlowDO = decisionFlowDOMapper.selectByUuid(policyUuid);
        if(decisionFlowDO == null) {
            return null;
        }

        DecisionFlowDTO decisionFlowDTO = new DecisionFlowDTO();
        BeanUtils.copyProperties(decisionFlowDO,decisionFlowDTO);

        return decisionFlowDTO;
    }
}
