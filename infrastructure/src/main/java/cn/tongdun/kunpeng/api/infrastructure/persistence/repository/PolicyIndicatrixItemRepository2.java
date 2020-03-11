package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;


import cn.tongdun.kunpeng.api.engine.dto.PolicyIndicatrixItemDTO;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.IPolicyIndicatrixItemRepository2;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyIndicatrixItemDOMapper;
import cn.tongdun.kunpeng.share.dataobject.PolicyIndicatrixItemDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2019/12/18 上午11:42
 */
@Repository
public class PolicyIndicatrixItemRepository2 implements IPolicyIndicatrixItemRepository2 {

    private Logger logger = LoggerFactory.getLogger(PolicyIndicatrixItemRepository2.class);


    @Autowired
    private PolicyIndicatrixItemDOMapper policyIndicatrixItemDOMapper;


    /**
     * 根据策略查询策略指标定义
     */
    @Override
    public List<PolicyIndicatrixItemDTO> queryByPolicyUuid(String policyUuid){
        List<PolicyIndicatrixItemDO> policyIndicatrixItemDOList = policyIndicatrixItemDOMapper.selectEnabledByPolicyUuid(policyUuid);
        if(policyIndicatrixItemDOList == null) {
            return null;
        }

        List<PolicyIndicatrixItemDTO> result = null;
        result = policyIndicatrixItemDOList.stream().map(policyIndicatrixItemDO->{
            PolicyIndicatrixItemDTO policyIndicatrixItemDTO = new PolicyIndicatrixItemDTO();
            BeanUtils.copyProperties(policyIndicatrixItemDO,policyIndicatrixItemDTO);
            return policyIndicatrixItemDTO;
        }).collect(Collectors.toList());

        return result;
    }


    /**
     * 根据uuid查询策略指标定义
     */
    @Override
    public PolicyIndicatrixItemDTO queryByUuid(String uuid){
        PolicyIndicatrixItemDO policyIndicatrixItemDO = policyIndicatrixItemDOMapper.selectByUuid(uuid);
        if(policyIndicatrixItemDO == null){
            return null;
        }

        PolicyIndicatrixItemDTO policyIndicatrixItemDTO = new PolicyIndicatrixItemDTO();
        BeanUtils.copyProperties(policyIndicatrixItemDO,policyIndicatrixItemDTO);
        return policyIndicatrixItemDTO;
    }

}
