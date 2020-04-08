package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyDecisionModeDO;


public interface PolicyDecisionModeDAO {


    PolicyDecisionModeDO selectByPolicyUuid(String policyUuid);

    PolicyDecisionModeDO selectByUuid(String uuid);

}