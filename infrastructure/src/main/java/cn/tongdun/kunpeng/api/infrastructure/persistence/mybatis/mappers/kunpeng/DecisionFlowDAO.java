package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.DecisionFlowDO;

public interface DecisionFlowDOMapper {

    DecisionFlowDO selectByUuid(String uuid);

}