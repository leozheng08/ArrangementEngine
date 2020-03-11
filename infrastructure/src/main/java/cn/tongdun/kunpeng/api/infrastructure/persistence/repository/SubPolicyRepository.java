package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;


import cn.tongdun.kunpeng.api.engine.dto.*;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.ISubPolicyRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.*;
import cn.tongdun.kunpeng.share.dataobject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2019/12/18 上午11:42
 */
@Repository
public class SubPolicyRepository implements ISubPolicyRepository{

    private Logger logger = LoggerFactory.getLogger(SubPolicyRepository.class);


    @Autowired
    private PolicyChallengerDOMapper policyChallengerDOMapper;
    @Autowired
    private SubPolicyDOMapper subPolicyDOMapper;
    @Autowired
    private PolicyIndexRepository policyIndexRepository;
    @Autowired
    private RuleRepository ruleRepository;


    /**
     * 根据策略uuid查询子策略列表，包含策略的各个子对象的完整信息，
     * @param policyUuid
     * @return
     */
    @Override
    public List<SubPolicyDTO> queryFullByPolicyUuid(String policyUuid){
        List<SubPolicyDO> subPolicyDOList = subPolicyDOMapper.selectListByPolicyUuid(policyUuid);

        if(subPolicyDOList == null) {
            return null;
        }

        List<SubPolicyDTO> result = null;
        result = subPolicyDOList.stream().map(subPolicyDO->{
            SubPolicyDTO subPolicyDTO = new SubPolicyDTO();
            BeanUtils.copyProperties(subPolicyDO,subPolicyDTO);
            return subPolicyDTO;
        }).collect(Collectors.toList());

        for(SubPolicyDTO subPolicyDTO :result){
            subPolicyDTO.setRules(ruleRepository.queryFullBySubPolicyUuid(subPolicyDTO.getUuid()));
            subPolicyDTO.setIndexDefinitionList(policyIndexRepository.queryBySubPolicyUuid(subPolicyDTO.getUuid()));
        }

        return result;
    }


    /**
     * 查询单个策略的信息，包含各个子对象
     * @param uuid
     * @return
     */
    @Override
    public SubPolicyDTO queryFullByUuid(String uuid){
        SubPolicyDO subPolicyDO = subPolicyDOMapper.selectByUuid(uuid);

        if(subPolicyDO == null) {
            return null;
        }
        SubPolicyDTO subPolicyDTO = new SubPolicyDTO();
        BeanUtils.copyProperties(subPolicyDO,subPolicyDTO);

        subPolicyDTO.setRules(ruleRepository.queryFullBySubPolicyUuid(subPolicyDTO.getUuid()));
        subPolicyDTO.setIndexDefinitionList(policyIndexRepository.queryBySubPolicyUuid(subPolicyDTO.getUuid()));

        return subPolicyDTO;
    }

    /**
     * 查询单个策略的信息
     * @param uuid
     * @return
     */
    @Override
    public SubPolicyDTO queryByUuid(String uuid){
        SubPolicyDO subPolicyDO = subPolicyDOMapper.selectByUuid(uuid);

        if(subPolicyDO == null) {
            return null;
        }
        SubPolicyDTO subPolicyDTO = new SubPolicyDTO();
        BeanUtils.copyProperties(subPolicyDO,subPolicyDTO);
        return subPolicyDTO;
    }

}
