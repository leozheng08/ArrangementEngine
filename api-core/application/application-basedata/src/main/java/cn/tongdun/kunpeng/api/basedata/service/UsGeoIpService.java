package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;

import java.util.List;
import java.util.Map;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:02 下午
 */
@Extension(tenant = "us",business = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class UsGeoIpService implements GeoIpService{

    @Override
    public GeoipEntity getIpInfo(String ip) {
        return null;
    }

    @Override
    public Map<String, GeoipEntity> batchGetIpInfo(List<String> ipList) {
        return null;
    }
}
