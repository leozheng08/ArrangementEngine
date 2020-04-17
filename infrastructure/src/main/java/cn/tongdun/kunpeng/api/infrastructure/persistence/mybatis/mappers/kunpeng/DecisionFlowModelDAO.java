package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.DecisionFlowModelDO;

import java.util.List;

public interface DecisionFlowModelDAO {

    List<DecisionFlowModelDO> selectByDecisionFlowUuid(String decisionFlowUuid);
}