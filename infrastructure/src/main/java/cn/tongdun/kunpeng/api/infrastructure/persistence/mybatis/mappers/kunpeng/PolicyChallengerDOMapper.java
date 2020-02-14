package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyChallengerDO;

public interface PolicyChallengerDOMapper {

    PolicyChallengerDO selectByPolicyDefinitionUuid(String policyDefinitionUuid);

}