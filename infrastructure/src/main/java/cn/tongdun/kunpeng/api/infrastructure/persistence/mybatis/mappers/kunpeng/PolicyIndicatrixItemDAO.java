package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyIndicatrixItemDO;

import java.util.List;

public interface PolicyIndicatrixItemDOMapper {

    /**
     * @mbggenerated
     */
    List<PolicyIndicatrixItemDO> selectEnabledByPolicyUuid(String policyUuid);

    PolicyIndicatrixItemDO selectByUuid(String uuid);

}