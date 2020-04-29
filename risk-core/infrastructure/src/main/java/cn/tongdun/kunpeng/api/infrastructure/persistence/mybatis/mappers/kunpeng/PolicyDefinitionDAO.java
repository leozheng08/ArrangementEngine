package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.api.engine.reload.docache.Cacheable;
import cn.tongdun.kunpeng.share.dataobject.PolicyDefinitionDO;

import java.util.List;
import java.util.Set;

/**
 * @author zhengwei
 * @date 2019-12-27 18:38
 **/
public interface PolicyDefinitionDAO {

    @Cacheable
    PolicyDefinitionDO selectByUuid(String uuid);

    PolicyDefinitionDO selectByPolicyUuid(String policyUuid);

    @Cacheable(idxName = "partner" )
    List<PolicyDefinitionDO> selectByPartners(Set<String> partners);

    @Cacheable(idxName = "partner" )
    List<PolicyDefinitionDO> selectByPartner(String partner);

    //todo 后期优化，按分页查询所有
    @Cacheable(idxName = "all" )
    List<PolicyDefinitionDO> selectAll();
}
