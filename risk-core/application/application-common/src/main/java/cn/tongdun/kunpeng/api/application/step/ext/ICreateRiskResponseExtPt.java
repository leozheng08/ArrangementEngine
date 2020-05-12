package cn.tongdun.kunpeng.api.application.step.ext;

import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * @Author: liang.chen
 * @Date: 2020/3/2 上午2:42
 */

public interface ICreateRiskResponseExtPt extends IExtensionPoint {

    IRiskResponse createRiskResponse(FraudContext context );
}
