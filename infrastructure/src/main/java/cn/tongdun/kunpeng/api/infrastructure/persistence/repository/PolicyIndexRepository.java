package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;


import cn.tongdun.kunpeng.api.engine.dto.IndexDefinitionDTO;
import cn.tongdun.kunpeng.api.engine.model.policyindex.IPolicyIndexRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.IndexDefinitionDAO;
import cn.tongdun.kunpeng.share.dataobject.IndexDefinitionDO;
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
    private IndexDefinitionDAO indexDefinitionDAO;


    /**
     * 根据策略查询策略指标定义
     */
    @Override
    public List<IndexDefinitionDTO> queryByPolicyUuid(String policyUuid){
        List<IndexDefinitionDO> indexDefinitionDOList = indexDefinitionDAO.selectEnabledIndexesByPolicyUuid(policyUuid);
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
        List<IndexDefinitionDO> indexDefinitionDOList = indexDefinitionDAO.selectEnabledIndexesBySubPolicyUuid(subPolicyUuid);
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
        IndexDefinitionDO indexDefinitionDO = indexDefinitionDAO.selectByUuid(uuid);
        if(indexDefinitionDO == null){
            return null;
        }

        IndexDefinitionDTO indexDefinitionDTO = new IndexDefinitionDTO();
        BeanUtils.copyProperties(indexDefinitionDO,indexDefinitionDTO);
        return indexDefinitionDTO;
    }

}
