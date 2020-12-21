package cn.tongdun.kunpeng.api.application.platformindex;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * 指标平台对接扩展点
 * @author jie
 * @date 2020/12/14
 */
public interface IndicatrixServiceExtPt extends IExtensionPoint {

    /**
     * 调用指标平台对接接口
     *
     * @param fraudContext 上下文
     */
    boolean calculate(AbstractFraudContext fraudContext);
}
