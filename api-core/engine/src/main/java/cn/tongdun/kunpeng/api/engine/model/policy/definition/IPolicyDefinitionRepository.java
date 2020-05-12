package cn.tongdun.kunpeng.api.engine.model.policy.definition;


import java.util.List;
import java.util.Set;

public interface IPolicyDefinitionRepository {

    //取得所有策略定义清单
    List<PolicyDefinition> queryByPartners(Set<String> partners);


    //根据合作方取得所有策略定义清单
    List<PolicyDefinition> queryByPartner(String partner);


    //根据策略定义uuid查询
    PolicyDefinition queryByUuid(String uuid);


    //根据策略uuid查询
    PolicyDefinition queryByPolicyUuid(String uuid);
}
