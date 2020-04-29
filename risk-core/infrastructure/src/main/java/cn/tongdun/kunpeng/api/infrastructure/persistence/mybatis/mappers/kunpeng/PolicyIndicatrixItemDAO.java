package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.api.engine.reload.docache.Cacheable;
import cn.tongdun.kunpeng.share.dataobject.PolicyIndicatrixItemDO;

import java.util.List;

public interface PolicyIndicatrixItemDAO {

    //todo 后期优化，按分页查询所有
    List<PolicyIndicatrixItemDO> selectAll();
    /**
     * @mbggenerated
     */
    List<PolicyIndicatrixItemDO> selectEnabledByPolicyUuid(String policyUuid);

    @Cacheable
    PolicyIndicatrixItemDO selectByUuid(String uuid);

}