package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.RuleConditionElementDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RuleConditionElementDOMapper {

    List<RuleConditionElementDO> selectByBizUuidBizType(@Param("bizUuid") String bizUuid, @Param("bizType") String bizType);

}