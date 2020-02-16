package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyDecisionModeDO;


public interface PolicyDecisionModeDOMapper {

    /**
     *
     * @mbggenerated
     */
    PolicyDecisionModeDO selectByPolicyUuid(String policyUuid);


}