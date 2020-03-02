package cn.tongdun.kunpeng.api.application.output.ext;

import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * 通用的输出
 * @Author: liang.chen
 * @Date: 2020/2/10 下午3:11
 */
public interface IRuleDetailOutputExtPt extends IExtensionPoint {
    boolean ruleDetailOutput(AbstractFraudContext context, IRiskResponse response);
}
