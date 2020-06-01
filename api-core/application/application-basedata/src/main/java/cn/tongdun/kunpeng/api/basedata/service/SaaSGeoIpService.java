package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.tongdun.gaea.dubbo.GpsQueryService;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:17 下午
 */
@Extension(tenant = BizScenario.DEFAULT, business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class SaaSGeoIpService implements GeoIpService {

    @Autowired
    private GpsQueryService gpsQueryService;

    @Override
    public GeoipEntity getIpInfo(String ip) {
        return null;
    }

    @Override
    public Map<String, GeoipEntity> batchGetIpInfo(List<String> ipList) {
        return null;
    }
}
