package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyFieldItemDO;

import java.util.List;

public interface PolicyFieldItemDAO {

    List<PolicyFieldItemDO> selectByPolicyUuid(String policyUuid);
}