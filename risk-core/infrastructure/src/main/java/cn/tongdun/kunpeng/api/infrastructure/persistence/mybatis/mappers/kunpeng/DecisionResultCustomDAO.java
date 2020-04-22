package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.DecisionResultCustomDO;

/**
 * @author zhengwei
 * @date 2020-01-17 15:50
 **/
public interface DecisionResultCustomDAO {

    DecisionResultCustomDO selectByUuid(String uuid);

    DecisionResultCustomDO selectByPolicyUuid(String policyUuid);
}
