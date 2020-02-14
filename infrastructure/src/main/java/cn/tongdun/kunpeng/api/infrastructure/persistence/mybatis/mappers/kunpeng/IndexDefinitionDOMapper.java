package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.IndexDefinitionDO;

import java.util.List;

public interface IndexDefinitionDOMapper {

    List<IndexDefinitionDO> getEnabledIndexesBySubPolicyUuid(String subPolicyUuid);
}
