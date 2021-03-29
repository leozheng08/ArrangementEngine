package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyDefinitionDO;

import java.util.List;
import java.util.Set;

/**
 * @author zhengwei
 * @date 2019-12-27 18:38
 **/
public interface PolicyDefinitionDAO {

    PolicyDefinitionDO selectByUuid(String uuid);

    PolicyDefinitionDO selectByPolicyUuid(String policyUuid);

    List<PolicyDefinitionDO> selectByPartners(Set<String> partners);

    List<PolicyDefinitionDO> selectByPartner(String partner);

    //todo 后期优化，按分页查询所有
    List<PolicyDefinitionDO> selectAll();
}
