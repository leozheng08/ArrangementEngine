package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;


import cn.tongdun.kunpeng.api.engine.dto.*;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.IPolicyChallengerRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.*;
import cn.tongdun.kunpeng.share.dataobject.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/18 上午11:42
 */
@Repository
public class PolicyChallengerRepository implements IPolicyChallengerRepository {

    private Logger logger = LoggerFactory.getLogger(PolicyChallengerRepository.class);


    @Autowired
    private PolicyChallengerDOMapper policyChallengerDOMapper;


    //根据PolicyDefinitionUuid查询挑战者
    @Override
    public PolicyChallengerDTO queryByPolicyDefinitionUuid(String policyDefinitionUuid){
        PolicyChallengerDO policyChallengerDO = policyChallengerDOMapper.selectByPolicyDefinitionUuid(policyDefinitionUuid);
        if(policyChallengerDO == null) {
            return null;
        }

        PolicyChallengerDTO policyChallengerDTO = new PolicyChallengerDTO();
        BeanUtils.copyProperties(policyChallengerDO,policyChallengerDTO);

        return policyChallengerDTO;
    }


    //根据uuid查询挑战者
    @Override
    public PolicyChallengerDTO queryByUuid(String uuid){
        PolicyChallengerDO policyChallengerDO = policyChallengerDOMapper.selectByUuid(uuid);
        if(policyChallengerDO == null) {
            return null;
        }

        PolicyChallengerDTO policyChallengerDTO = new PolicyChallengerDTO();
        BeanUtils.copyProperties(policyChallengerDO,policyChallengerDTO);

        return policyChallengerDTO;
    }
}
