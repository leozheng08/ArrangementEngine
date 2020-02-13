package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyDO;

import java.util.List;

public interface PolicyDOMapper {


    PolicyDO selectByUuid(String uuid);

    List<PolicyDO> selectByPolicyDefinitionUuid(String policyDefinitionUuid);

    /**
     * 根据源头策略uuid查询版本号
     *
     * @param originPolicyUuid 源头策略uuid
     * @return
     */
    List<String> selectVersionByOriginIncludeDeleted(String originPolicyUuid);
}