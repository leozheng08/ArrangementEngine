package cn.tongdun.kunpeng.api.application.step.ext;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * @author: yuanhang
 * @date: 2020-07-28 17:58
 **/
public interface ISendKafkaExtPt extends IExtensionPoint {

    boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request);

}
