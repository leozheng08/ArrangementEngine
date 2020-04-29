package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.api.engine.reload.docache.Cacheable;
import cn.tongdun.kunpeng.share.dataobject.RuleDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RuleDAO {

    @Cacheable
    RuleDO selectByUuid(String uuid);

    RuleDO selectEnabledByUuid(String uuid);

    //todo 后期优化，按分页查询所有
    List<RuleDO>  selectAll();

    @Cacheable(idxName = "uuid")
    List<RuleDO>  selectByUuids(List<String> uuids);

    @Cacheable(idxName = "bizType_bizUuid")
    List<RuleDO> selectByBizTypeBizUuid(@Param("bizType") String bizType, @Param("bizUuid") String bizUuid);

}