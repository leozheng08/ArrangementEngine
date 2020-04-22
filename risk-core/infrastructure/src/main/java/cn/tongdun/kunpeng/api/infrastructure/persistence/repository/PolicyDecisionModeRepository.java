package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;


import cn.tongdun.kunpeng.api.engine.dto.*;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.IPolicyDecisionModeRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.*;
import cn.tongdun.kunpeng.share.dataobject.*;
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
public class PolicyDecisionModeRepository implements IPolicyDecisionModeRepository{

    private Logger logger = LoggerFactory.getLogger(PolicyDecisionModeRepository.class);

    @Autowired
    private PolicyDecisionModeDAO policyDecisionModeDAO;

    /**
     * 查询策略当前在用的运行模式
     */
    @Override
    public PolicyDecisionModeDTO queryByPolicyUuid(String policyUuid){
        PolicyDecisionModeDO policyDecisionModeDO = policyDecisionModeDAO.selectByPolicyUuid(policyUuid);
        if(policyDecisionModeDO == null) {
            return null;
        }

        PolicyDecisionModeDTO policyDecisionModeDTO = new PolicyDecisionModeDTO();
        BeanUtils.copyProperties(policyDecisionModeDO,policyDecisionModeDTO);

        return policyDecisionModeDTO;
    }


    /**
     * 根据uuid查询运行模式
     * @param uuid
     * @return
     */
    @Override
    public PolicyDecisionModeDTO queryByUuid(String uuid){
        PolicyDecisionModeDO policyDecisionModeDO = policyDecisionModeDAO.selectByUuid(uuid);
        if(policyDecisionModeDO == null) {
            return null;
        }

        PolicyDecisionModeDTO policyDecisionModeDTO = new PolicyDecisionModeDTO();
        BeanUtils.copyProperties(policyDecisionModeDO,policyDecisionModeDTO);

        return policyDecisionModeDTO;
    }

}
