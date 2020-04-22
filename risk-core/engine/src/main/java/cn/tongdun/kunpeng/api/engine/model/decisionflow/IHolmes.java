package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;

public interface IHolmes {

    /**
     * 调用模型平台接口
     *
     * @param fraudContext 上下文
     * @param decisionFlowModel
     */
    boolean calculate(AbstractFraudContext fraudContext, DecisionFlowModel decisionFlowModel);

}
