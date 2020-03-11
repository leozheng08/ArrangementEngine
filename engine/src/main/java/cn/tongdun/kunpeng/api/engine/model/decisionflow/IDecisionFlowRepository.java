package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import cn.tongdun.kunpeng.api.engine.dto.DecisionFlowDTO;

public interface IDecisionFlowRepository {

    /**
     * 查询决策流, 决策流的uuid与策略的uuid为值相同
     */
    DecisionFlowDTO queryByUuid(String policyUuid);
}
