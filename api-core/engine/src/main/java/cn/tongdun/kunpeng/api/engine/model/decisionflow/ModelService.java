package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;

public interface ModelService {

    /**
     * 调用模型平台接口
     *
     * @param fraudContext 上下文
     * @param configInfo
     */
    boolean calculate(AbstractFraudContext fraudContext, ModelConfigInfo configInfo);

}
