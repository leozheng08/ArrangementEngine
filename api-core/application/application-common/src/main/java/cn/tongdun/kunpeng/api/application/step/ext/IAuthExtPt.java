package cn.tongdun.kunpeng.api.application.step.ext;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * @author: yuanhang
 * @date: 2020-07-28 13:34
 **/
public interface IAuthExtPt extends IExtensionPoint {

    boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request);

}
