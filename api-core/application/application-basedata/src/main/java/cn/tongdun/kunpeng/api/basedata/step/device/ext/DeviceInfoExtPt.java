package cn.tongdun.kunpeng.api.basedata.step.device.ext;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * 设备指纹信息获取扩展点
 * @author jie
 * @date 2021/1/20
 */
public interface DeviceInfoExtPt extends IExtensionPoint {

    boolean fetchData(AbstractFraudContext context, IRiskResponse response, RiskRequest request);
}
