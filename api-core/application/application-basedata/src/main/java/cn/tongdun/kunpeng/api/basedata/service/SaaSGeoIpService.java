package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.elfin.biz.intf.BaseDataQueryService;
import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.extension.Extension;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:17 下午
 */
@Extension(tenant = BizScenario.DEFAULT, business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class SaaSGeoIpService implements GeoIpServiceExtPt {

    private static final Logger logger = LoggerFactory.getLogger(SaaSGeoIpService.class);

    @Autowired
    private BaseDataQueryService baseDataQueryService;

    @Override
    public GeoipEntity getIpInfo(String ip, AbstractFraudContext context) {
        try {
            if (StringUtils.isBlank(ip)) {
                logger.info(TraceUtils.getFormatTrace() + "elfinBaseDateService get geoentity from elfin with params null");
                return null;
            }
            cn.fraudmetrix.elfin.biz.entity.GeoipEntity geoipEntity = baseDataQueryService.getIpInfo(ip);
            return copyGeoipEntityProperties(geoipEntity);
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "ip query geoinfo failed ip {}", ip, e);
        }
        return null;
    }

    private GeoipEntity copyGeoipEntityProperties(cn.fraudmetrix.elfin.biz.entity.GeoipEntity geoipEntity) {
        //springbeanutils反射有性能问题
        GeoipEntity oldGeoipEntity = new GeoipEntity();
        if (geoipEntity == null) {
            return null;
        }
        oldGeoipEntity.setIsp(geoipEntity.getIsp());
        oldGeoipEntity.setCity(geoipEntity.getCity());
        oldGeoipEntity.setProvince(geoipEntity.getProvince());
        oldGeoipEntity.setAddress(geoipEntity.getAddress());
        oldGeoipEntity.setArea(geoipEntity.getArea());
        oldGeoipEntity.setAreaId(geoipEntity.getAreaId());
        oldGeoipEntity.setCityId(geoipEntity.getCityId());
        oldGeoipEntity.setCountryId(geoipEntity.getCountryId());
        oldGeoipEntity.setCountry(geoipEntity.getCountry());
        oldGeoipEntity.setDesc(geoipEntity.getDesc());
        oldGeoipEntity.setType(geoipEntity.getType());
        oldGeoipEntity.setProvinceId(geoipEntity.getProvinceId());
        oldGeoipEntity.setIspId(geoipEntity.getIspId());
        oldGeoipEntity.setLongitude(geoipEntity.getLongitude());
        oldGeoipEntity.setLatitude(geoipEntity.getLatitude());
        oldGeoipEntity.setLip(geoipEntity.getLip());
        oldGeoipEntity.setIp(geoipEntity.getIp());
        oldGeoipEntity.setExtra1(geoipEntity.getExtra1());
        oldGeoipEntity.setExtra2(geoipEntity.getExtra2());
        oldGeoipEntity.setCounty(geoipEntity.getCounty());
        oldGeoipEntity.setCountyId(geoipEntity.getCountyId());
        return oldGeoipEntity;
    }

    @Override
    public Map<String, GeoipEntity> batchGetIpInfo(List<String> ips) {
        if (CollectionUtils.isEmpty(ips)) {
            logger.warn(TraceUtils.getFormatTrace() + "getIpInfos from elfin with params empty");
            return Collections.emptyMap();
        }
        final Map<String, GeoipEntity> finalResultMap = Maps.newHashMapWithExpectedSize(ips.size());
        Map<String, cn.fraudmetrix.elfin.biz.entity.GeoipEntity> ipInfos = null;
        try {
            ipInfos = baseDataQueryService.getIpInfos(ips);
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "getIpInfos error, ipList={}", ips, e);
            return finalResultMap;
        }
        if (MapUtils.isEmpty(ipInfos)) {
            return finalResultMap;
        }
        Set<Map.Entry<String, cn.fraudmetrix.elfin.biz.entity.GeoipEntity>> entries = ipInfos.entrySet();
        for (Map.Entry<String, cn.fraudmetrix.elfin.biz.entity.GeoipEntity> entry : entries) {
            cn.fraudmetrix.elfin.biz.entity.GeoipEntity value = entry.getValue();
            GeoipEntity geoipEntity = this.copyGeoipEntityProperties(value);
            finalResultMap.put(entry.getKey(), geoipEntity);
        }

        return finalResultMap;
    }
}
