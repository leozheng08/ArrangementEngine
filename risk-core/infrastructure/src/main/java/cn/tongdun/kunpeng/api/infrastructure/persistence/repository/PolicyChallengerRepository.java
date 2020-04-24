package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;


import cn.tongdun.kunpeng.api.engine.dto.*;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.IPolicyChallengerRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.PolicyChallenger;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.*;
import cn.tongdun.kunpeng.share.dataobject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2019/12/18 上午11:42
 */
@Repository
public class PolicyChallengerRepository implements IPolicyChallengerRepository {

    private Logger logger = LoggerFactory.getLogger(PolicyChallengerRepository.class);


    @Autowired
    private PolicyChallengerDAO policyChallengerDAO;


    //根据合作方取得所有有效的挑战者任务
    @Override
    public List<PolicyChallenger> queryAvailableByPartners(Set<String> partners){
        List<PolicyChallengerDO> list = policyChallengerDAO.selectAvailableByPartners(partners);

        List<PolicyChallenger> result = null;
        if(list != null){
            result = list.stream().map(policyChallengerDO->{
                PolicyChallenger policyChallenger = new PolicyChallenger();
                BeanUtils.copyProperties(policyChallengerDO,policyChallenger);
                return policyChallenger;
            }).collect(Collectors.toList());
        }
        return result;
    }

    //根据PolicyDefinitionUuid查询挑战者
    @Override
    public PolicyChallenger queryByPolicyDefinitionUuid(String policyDefinitionUuid){
        PolicyChallengerDO policyChallengerDO = policyChallengerDAO.selectByPolicyDefinitionUuid(policyDefinitionUuid);
        if(policyChallengerDO == null) {
            return null;
        }

        PolicyChallenger policyChallenger = new PolicyChallenger();
        BeanUtils.copyProperties(policyChallengerDO,policyChallenger);

        return policyChallenger;
    }


    //根据uuid查询挑战者
    @Override
    public PolicyChallenger queryByUuid(String uuid){
        PolicyChallengerDO policyChallengerDO = policyChallengerDAO.selectByUuid(uuid);
        if(policyChallengerDO == null) {
            return null;
        }

        PolicyChallenger policyChallenger = new PolicyChallenger();
        BeanUtils.copyProperties(policyChallengerDO,policyChallenger);

        return policyChallenger;
    }
}
