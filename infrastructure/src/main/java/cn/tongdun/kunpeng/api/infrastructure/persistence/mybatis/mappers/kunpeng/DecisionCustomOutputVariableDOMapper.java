package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.DecisionCustomOutputVariableDO;

import java.util.List;

public interface DecisionCustomOutputVariableDOMapper {

    List<DecisionCustomOutputVariableDO> selectByOutputUuid(String outputUuid);

}