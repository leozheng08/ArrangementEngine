package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.RuleDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RuleDOMapper {

    RuleDO selectByUuid(String uuid);

    List<RuleDO>  selectByUuids(List<String> uuids);

    List<RuleDO> selectByBizUuidBizType(@Param("bizUuid") String bizUuid, @Param("bizType") String bizType);

}