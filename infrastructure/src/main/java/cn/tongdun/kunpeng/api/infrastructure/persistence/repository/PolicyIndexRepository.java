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


    private PolicyChallengerDOMapper policyChallengerDOMapper;
    @Autowired
    private SubPolicyDOMapper subPolicyDOMapper;
    @Autowired
    private PolicyIndicatrixItemDOMapper policyIndicatrixItemDOMapper;

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
            subPolicyDTO.setIndexDefinitionList(queryIndexDefinitionDTOBySubPolicyUuid(subPolicyDTO.getUuid()));
        }

        return result;
    }



    //查询策略指标
    private List<IndexDefinitionDTO> queryIndexDefinitionDTOBySubPolicyUuid(String subPolicyUuid){
        List<IndexDefinitionDO> indexDefinitionDOList = indexDefinitionDOMapper.getEnabledIndexesBySubPolicyUuid(subPolicyUuid);
        if(indexDefinitionDOList == null) {
            return null;
        }

        List<IndexDefinitionDTO> result = null;
        result = indexDefinitionDOList.stream().map(indexDefinitionDO->{
            IndexDefinitionDTO indexDefinitionDTO = new IndexDefinitionDTO();
            BeanUtils.copyProperties(indexDefinitionDO,indexDefinitionDTO);
            return indexDefinitionDTO;
        }).collect(Collectors.toList());

        return result;
    }

}
