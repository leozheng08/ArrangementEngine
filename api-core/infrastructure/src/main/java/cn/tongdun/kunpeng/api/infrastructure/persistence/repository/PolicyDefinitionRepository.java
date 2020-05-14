package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.policy.definition.IPolicyDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyDefinitionDAO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDefinitionDO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class PolicyDefinitionRepository  implements IPolicyDefinitionRepository{

    @Autowired
    private PolicyDefinitionDAO policyDefinitionDAO;

    //取得所有策略定义清单
    @Override
    public List<PolicyDefinition> queryByPartners(Set<String> partners){
        if (null==partners||partners.isEmpty()){
            return Collections.emptyList();
        }
        List<PolicyDefinitionDO> list = policyDefinitionDAO.selectByPartners(partners);

        List<PolicyDefinition> result = null;
        if(list != null){
            result = list.stream().map(policyDefinitionDO->{
                PolicyDefinition policyDefinition = new PolicyDefinition();
                BeanUtils.copyProperties(policyDefinitionDO,policyDefinition);
                return policyDefinition;
            }).collect(Collectors.toList());
        }
        return result;
    }


    //根据合作方取得所有策略定义清单
    @Override
    public List<PolicyDefinition> queryByPartner(String partner){
        List<PolicyDefinitionDO> list = policyDefinitionDAO.selectByPartner(partner);

        List<PolicyDefinition> result = null;
        if(list != null){
            result = list.stream().map(policyDefinitionDO->{
                PolicyDefinition policyDefinition = new PolicyDefinition();
                BeanUtils.copyProperties(policyDefinitionDO,policyDefinition);
                return policyDefinition;
            }).collect(Collectors.toList());
        }
        return result;
    }


    //根据策略定义uuid查询
    @Override
    public PolicyDefinition queryByUuid(String uuid){
        PolicyDefinitionDO policyDefinitionDO= policyDefinitionDAO.selectByUuid(uuid);
        if(policyDefinitionDO == null){
            return null;
        }
        PolicyDefinition policyDefinition = new PolicyDefinition();
        BeanUtils.copyProperties(policyDefinitionDO,policyDefinition);
        return policyDefinition;
    }


    //根据策略uuid查询
    @Override
    public PolicyDefinition queryByPolicyUuid(String uuid){
        PolicyDefinitionDO policyDefinitionDO= policyDefinitionDAO.selectByPolicyUuid(uuid);
        if(policyDefinitionDO == null){
            return null;
        }
        PolicyDefinition policyDefinition = new PolicyDefinition();
        BeanUtils.copyProperties(policyDefinitionDO,policyDefinition);
        return policyDefinition;
    }
}
