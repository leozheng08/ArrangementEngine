package cn.tongdun.kunpeng.api.basedata.service;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.GeoipEntity;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * @Author: liuq
 * @Date: 2020/5/29 1:49 下午
 */
public interface GeoIpServiceExtPt extends IExtensionPoint {

    GeoipEntity getIpInfo(String ip, AbstractFraudContext context);

}
