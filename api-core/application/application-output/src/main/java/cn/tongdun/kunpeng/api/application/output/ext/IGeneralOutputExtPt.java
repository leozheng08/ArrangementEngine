package cn.tongdun.kunpeng.api.application.output.ext;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * 通用的输出
 * @Author: liang.chen
 * @Date: 2020/2/10 下午3:11
 */
public interface IGeneralOutputExtPt extends IExtensionPoint {
    boolean generalOutput(AbstractFraudContext context, IRiskResponse response, RiskRequest request);
}
