package cn.tongdun.kunpeng.api.basedata.service;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:41 下午
 */
public interface BinInfoServiceExtPt extends IExtensionPoint {

    boolean getBinInfo(AbstractFraudContext context, IRiskResponse response, RiskRequest request);

}
