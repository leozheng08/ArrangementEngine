package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;

public interface IGenericDubboCaller {

    /**
     * 三方接口泛化调用
     * @param fraudContext
     * @param decisionFlowInterface 三方接口配置信息
     * @return
     */
    boolean call(AbstractFraudContext fraudContext,
                 DecisionFlowInterface decisionFlowInterface
    );
}
