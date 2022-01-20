package cn.tongdun.kunpeng.api.application.check.step.ext;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * 入参字段校验扩展点
 */
public interface BaseCheckParamsExtPt extends IExtensionPoint {

    boolean fetchData(AbstractFraudContext context, IRiskResponse response, RiskRequest request);
}
