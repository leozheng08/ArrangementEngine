package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyDefinitionDO;

/**
 * @author zhengwei
 * @date 2019-12-27 18:38
 **/
public interface PolicyDefinitionDOMapper {

    PolicyDefinitionDO selectByUuid(String uuid);

    PolicyDefinitionDO selectByPolicyUuid(String policyUuid);
}
