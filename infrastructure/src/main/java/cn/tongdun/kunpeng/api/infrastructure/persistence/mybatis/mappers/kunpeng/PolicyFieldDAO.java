package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyFieldDO;

import java.util.List;

public interface PolicyFieldDOMapper {

    /**
     *
     * @mbggenerated
     */
    List<PolicyFieldDO> selectByPolicyUuid(String policyUuid);

}