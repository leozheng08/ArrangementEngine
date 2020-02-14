package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.DecisionFlowInterfaceDO;

import java.util.List;

public interface DecisionFlowInterfaceDOMapper {

    List<DecisionFlowInterfaceDO> selectByDecisionFlowUuid(String decisionFlowUuid);

}