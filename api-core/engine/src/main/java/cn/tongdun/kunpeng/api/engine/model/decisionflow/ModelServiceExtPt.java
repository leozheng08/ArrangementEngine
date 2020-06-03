package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

public interface ModelServiceExtPt extends IExtensionPoint {

    /**
     * 调用模型平台接口
     *
     * @param fraudContext 上下文
     * @param configInfo
     */
    boolean calculate(AbstractFraudContext fraudContext, ModelConfigInfo configInfo);

}
