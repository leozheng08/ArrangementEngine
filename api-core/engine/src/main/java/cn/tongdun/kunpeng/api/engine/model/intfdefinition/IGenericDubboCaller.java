package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;

public interface IGenericDubboCaller {

    boolean call(AbstractFraudContext fraudContext, // 你懂的
                 DecisionFlowInterface decisionFlowInterface //接口配置信息
    );
}
