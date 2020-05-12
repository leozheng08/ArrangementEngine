package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.api.engine.reload.docache.Cacheable;
import cn.tongdun.kunpeng.share.dataobject.DecisionFlowDO;

import java.util.List;

public interface DecisionFlowDAO {

    @Cacheable
    DecisionFlowDO selectByUuid(String uuid);

    //todo 后期优化，按分页查询所有
    List<DecisionFlowDO> selectAll();
}