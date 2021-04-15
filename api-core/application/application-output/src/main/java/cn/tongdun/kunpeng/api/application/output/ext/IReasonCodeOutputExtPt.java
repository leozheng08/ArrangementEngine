package cn.tongdun.kunpeng.api.application.output.ext;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * @author: yuanhang
 * @date: 2021-03-18 09:51
 **/

public interface IReasonCodeOutputExtPt extends IExtensionPoint {

    boolean dealWithSubReasonCodes(AbstractFraudContext context, IRiskResponse response, RiskRequest request);

}
