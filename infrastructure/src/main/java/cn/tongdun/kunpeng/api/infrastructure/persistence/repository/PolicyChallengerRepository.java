package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;


import cn.tongdun.kunpeng.api.engine.dto.*;
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
public class PolicyChallengerRepository {

    private Logger logger = LoggerFactory.getLogger(PolicyChallengerRepository.class);


    @Autowired
    private PolicyChallengerDOMapper policyChallengerDOMapper;


    //查询挑战者
    public PolicyChallengerDTO queryPolicyChallengerDTOByPolicyDefinitionUuid(String policyDefinitionUuid){
        PolicyChallengerDO policyChallengerDO = policyChallengerDOMapper.selectByPolicyDefinitionUuid(policyDefinitionUuid);
        if(policyChallengerDO == null) {
            return null;
        }

        PolicyChallengerDTO policyChallengerDTO = new PolicyChallengerDTO();
        BeanUtils.copyProperties(policyChallengerDO,policyChallengerDTO);

        return policyChallengerDTO;
    }





}
