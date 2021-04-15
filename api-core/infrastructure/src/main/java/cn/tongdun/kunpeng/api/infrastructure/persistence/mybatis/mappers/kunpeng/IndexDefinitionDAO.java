package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.api.engine.reload.docache.Cacheable;
import cn.tongdun.kunpeng.share.dataobject.IndexDefinitionDO;

import java.util.List;

public interface IndexDefinitionDAO {

    //todo 后期优化，按分页查询所有
    List<IndexDefinitionDO> selectAll();

    List<IndexDefinitionDO> selectEnabledIndexesByPolicyUuid(String policyUuid);

    List<IndexDefinitionDO> selectEnabledIndexesBySubPolicyUuid(String subPolicyUuid);

    IndexDefinitionDO selectByUuid(String uuid);
}
