package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyFieldDO;

import java.util.List;

public interface PolicyFieldDAO {

    /**
     *
     * @mbggenerated
     */
    List<PolicyFieldDO> selectByPolicyUuid(String policyUuid);

}