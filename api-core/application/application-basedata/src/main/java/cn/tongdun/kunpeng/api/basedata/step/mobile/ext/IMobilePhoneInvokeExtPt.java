package cn.tongdun.kunpeng.api.basedata.step.mobile.ext;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * 调用手机画像
 *
 * @Author: liang.chen
 * @Date: 2020/2/10 下午3:12
 */
public interface IMobilePhoneInvokeExtPt extends IExtensionPoint {

    boolean fetchData(AbstractFraudContext context, IRiskResponse response, RiskRequest request);
}
