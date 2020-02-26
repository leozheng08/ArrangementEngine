package cn.tongdun.kunpeng.api.application.output.ext;

import cn.tongdun.kunpeng.client.data.RiskResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * 通用的输出
 * @Author: liang.chen
 * @Date: 2020/2/10 下午3:11
 */
public interface IGeneralOutputExtPt extends IExtensionPoint {
    boolean generalOutput(AbstractFraudContext context,RiskResponse response);
}