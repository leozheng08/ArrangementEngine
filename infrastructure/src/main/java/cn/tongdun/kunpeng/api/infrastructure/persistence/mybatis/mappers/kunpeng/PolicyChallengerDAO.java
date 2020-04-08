package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyChallengerDO;

import java.util.List;
import java.util.Set;

public interface PolicyChallengerDOMapper {

    PolicyChallengerDO selectByPolicyDefinitionUuid(String policyDefinitionUuid);

    PolicyChallengerDO selectByUuid(String uuid);

    List<PolicyChallengerDO> selectAvailableByPartners(Set<String> partners);
}