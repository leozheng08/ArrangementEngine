package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.tongdun.gaea.dubbo.GpsQueryService;
import cn.tongdun.gaea.factservice.domain.GpsInfoDTO;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.extension.Extension;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:02 下午
 */
@Extension(tenant = "us", business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class UsGeoIpService implements GeoIpServiceExtPt {

    private static final Logger logger = LoggerFactory.getLogger(SaaSGeoIpService.class);

    @Autowired
    private GpsQueryService gpsQueryService;

    @Override
    public GeoipEntity getIpInfo(String ip) {
        if (StringUtils.isBlank(ip)) {
            return null;
        }
        Map<String, GpsInfoDTO> gpsInfoDTOMap = null;
        try {
            gpsInfoDTOMap = gpsQueryService.batchQueryGps(Lists.newArrayList(ip));
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "UsGeoIpService gpsQueryService.queryGps error,ip:" + ip, e);
        }
        if (MapUtils.isEmpty(gpsInfoDTOMap)) {
            return null;
        }
        return fromDTO2Entity(gpsInfoDTOMap.get(ip));
    }

    private GeoipEntity fromDTO2Entity(GpsInfoDTO gpsInfoDTO) {
        if (null == gpsInfoDTO) {
            return null;
        }
        GeoipEntity geoipEntity = new GeoipEntity();
        geoipEntity.setLongitude((float) gpsInfoDTO.getLongitude());
        geoipEntity.setLatitude((float) gpsInfoDTO.getLatitude());
        geoipEntity.setCountry(gpsInfoDTO.getCountry());
        geoipEntity.setCity(gpsInfoDTO.getCity());
        geoipEntity.setAddress(gpsInfoDTO.getAddress());
        geoipEntity.setArea(gpsInfoDTO.getArea());
        geoipEntity.setAreaId(gpsInfoDTO.getAreaId());
        geoipEntity.setCityId(gpsInfoDTO.getCityId());
        geoipEntity.setCountryId(gpsInfoDTO.getCountryId());
        geoipEntity.setCounty(gpsInfoDTO.getCounty());
        geoipEntity.setCountyId(gpsInfoDTO.getCountyId());
        geoipEntity.setDesc(gpsInfoDTO.getDesc());
        geoipEntity.setExtra1(geoipEntity.getExtra1());
        geoipEntity.setExtra2(geoipEntity.getExtra2());
        geoipEntity.setIp(gpsInfoDTO.getIp());
        geoipEntity.setIsp(gpsInfoDTO.getIsp());
        geoipEntity.setIspId(gpsInfoDTO.getIspId());
        geoipEntity.setLip(gpsInfoDTO.getLip());
        geoipEntity.setProvince(gpsInfoDTO.getProvince());
        geoipEntity.setProvinceId(gpsInfoDTO.getProvinceId());
        geoipEntity.setType(gpsInfoDTO.getType());

        return geoipEntity;
    }

    @Override
    public Map<String, GeoipEntity> batchGetIpInfo(List<String> ipList) {
        if (CollectionUtils.isEmpty(ipList)) {
            return Collections.emptyMap();
        }
        Map<String, GpsInfoDTO> gpsInfoDTOMap = null;
        try {
            gpsInfoDTOMap = gpsQueryService.batchQueryGps(ipList);
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "UsGeoIpService gpsQueryService.batchQueryGps error,ipList:" + ipList.toString(), e);
        }
        if (MapUtils.isEmpty(gpsInfoDTOMap)) {
            return Collections.emptyMap();
        }
        if (ipList.size() != gpsInfoDTOMap.size()) {
            logger.info(TraceUtils.getFormatTrace() + "ipList:" + ipList.toString() + ",gpsInfoDTOMap keySet:" + gpsInfoDTOMap.keySet().toString());
        }
        Map<String, GeoipEntity> geoipEntityMap = Maps.newHashMap();

        gpsInfoDTOMap.forEach((key, value) -> {
            geoipEntityMap.put(key, fromDTO2Entity(value));
        });
        return geoipEntityMap;
    }
}
