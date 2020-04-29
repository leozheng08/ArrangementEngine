package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.api.engine.reload.docache.Cacheable;
import cn.tongdun.kunpeng.share.dataobject.PolicyDecisionModeDO;

import java.util.List;


public interface PolicyDecisionModeDAO {

    @Cacheable(idxName = "policyUuid")
    PolicyDecisionModeDO selectByPolicyUuid(String policyUuid);

    @Cacheable
    PolicyDecisionModeDO selectByUuid(String uuid);

    //todo 后期优化，按分页查询所有
    List<PolicyDecisionModeDO> selectAll();

}