package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

import java.util.List;
import java.util.Map;

/**
 * @Author: liuq
 * @Date: 2020/5/29 1:49 下午
 */
public interface GeoIpServiceExtPt extends IExtensionPoint {

    GeoipEntity getIpInfo(String ip,AbstractFraudContext context);

    Map<String,GeoipEntity> batchGetIpInfo(List<String> ipList);

}
