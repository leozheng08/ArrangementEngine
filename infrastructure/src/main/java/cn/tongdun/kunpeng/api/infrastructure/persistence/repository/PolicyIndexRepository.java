package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;


import cn.tongdun.kunpeng.api.engine.dto.IndexDefinitionDTO;
import cn.tongdun.kunpeng.api.engine.dto.SubPolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.policyindex.IPolicyIndexRepository;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.ISubPolicyRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.IndexDefinitionDOMapper;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyChallengerDOMapper;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyIndicatrixItemDOMapper;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.SubPolicyDOMapper;
import cn.tongdun.kunpeng.share.dataobject.IndexDefinitionDO;
import cn.tongdun.kunpeng.share.dataobject.SubPolicyDO;
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
public class PolicyIndexRepository implements IPolicyIndexRepository{

    private Logger logger = LoggerFactory.getLogger(PolicyIndexRepository.class);


    @Autowired
    private IndexDefinitionDOMapper indexDefinitionDOMapper;


    /**
     * 根据策略查询策略指标定义
     */
    @Override
    public List<IndexDefinitionDTO> queryByPolicyUuid(String policyUuid){
        List<IndexDefinitionDO> indexDefinitionDOList = indexDefinitionDOMapper.selectEnabledIndexesByPolicyUuid(policyUuid);
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

    /**
     * 根据子策略查询策略指标定义
     */
    @Override
    public List<IndexDefinitionDTO> queryBySubPolicyUuid(String subPolicyUuid){
        List<IndexDefinitionDO> indexDefinitionDOList = indexDefinitionDOMapper.selectEnabledIndexesBySubPolicyUuid(subPolicyUuid);
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

    /**
     * 根据uuid查询策略指标定义
     */
    @Override
    public IndexDefinitionDTO queryByUuid(String uuid){
        IndexDefinitionDO indexDefinitionDO = indexDefinitionDOMapper.selectByUuid(uuid);
        if(indexDefinitionDO == null){
            return null;
        }

        IndexDefinitionDTO indexDefinitionDTO = new IndexDefinitionDTO();
        BeanUtils.copyProperties(indexDefinitionDO,indexDefinitionDTO);
        return indexDefinitionDTO;
    }

}
