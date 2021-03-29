package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.RuleDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RuleDAO {

    RuleDO selectByUuid(String uuid);

    RuleDO selectEnabledByUuid(String uuid);

    //todo 后期优化，按分页查询所有
    List<RuleDO>  selectAll();

    List<RuleDO>  selectByUuids(List<String> uuids);

    List<RuleDO> selectByBizTypeBizUuid(@Param("bizType") String bizType, @Param("bizUuid") String bizUuid);

}