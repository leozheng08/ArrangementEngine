package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.tongdun.evan.client.dubbo.AGeoipInfoQueryService;
import cn.tongdun.evan.client.entity.AGeoipEntity;
import cn.tongdun.evan.client.entity.AGeoipQueryDTO;
import cn.tongdun.evan.client.lang.Result;
import cn.tongdun.gaea.dubbo.GpsQueryService;
import cn.tongdun.gaea.factservice.domain.GpsInfoDTO;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.share.json.JSON;
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
import java.util.Objects;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:02 下午
 */
@Extension(tenant = "us", business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class UsGeoIpService implements GeoIpServiceExtPt {

    private static final Logger logger = LoggerFactory.getLogger(SaaSGeoIpService.class);

    @Autowired
    private GpsQueryService gpsQueryService;
    @Autowired
    private AGeoipInfoQueryService aGeoipInfoQueryService;


    @Override
    public GeoipEntity getIpInfo(String ip, AbstractFraudContext context) {
        if (StringUtils.isBlank(ip)) {
            return null;
        }
        Result<AGeoipEntity> result = null;
        try {
            AGeoipQueryDTO geoipQueryDTO = new AGeoipQueryDTO();
            geoipQueryDTO.setIp(ip);
            geoipQueryDTO.setPartnerCode(context.getPartnerCode());
            geoipQueryDTO.setSource("evan-us");
            geoipQueryDTO.setSeqId(context.getSeqId());
            logger.info("aGeoipInfoQueryService.queryGeoipInfo入参:{}", JSON.toJSONString(geoipQueryDTO));
            result = aGeoipInfoQueryService.queryGeoipInfo(geoipQueryDTO);
            logger.info("aGeoipInfoQueryService.queryGeoipInfo:{}", JSON.toJSONString(result));
        } catch (Exception e) {
            ReasonCodeUtil.add(context, ReasonCode.GEOIP_SERVICE_CALL_ERROR, "geoIp-us");
            logger.error(TraceUtils.getFormatTrace() + "UsGeoIpService gpsQueryService.queryGps error,ip:" + ip, e);
        }
        if (Objects.isNull(result)) {
            return null;
        }
        if (!result.isSuccess()) {
            switch (result.getCode()) {
                case "100":
                    ReasonCodeUtil.add(context, ReasonCode.GEOIP_PARAM_ERROR, "geoIp-us");
                    return null;
                case "102":
                    ReasonCodeUtil.add(context, ReasonCode.GEOIP_ILLEGAL_ERROR, "geoIp-us");
                    return null;
                case "403":
                    ReasonCodeUtil.add(context, ReasonCode.GEOIP_PERNISSION_ERROR, "geoIp-us");
                    return null;
                case "500":
                    ReasonCodeUtil.add(context, ReasonCode.GEOIP_SERRVER_ERROR, "geoIp-us");
                    return null;
                default:
                    return null;
            }
        }

        return fromDTO2EntityV2(result.getData());
    }

    private GeoipEntity fromDTO2EntityV2(AGeoipEntity aGeoipEntity) {
        if (null == aGeoipEntity) {
            return null;
        }
        GeoipEntity geoipEntity = new GeoipEntity();
        geoipEntity.setLongitude(Float.parseFloat(aGeoipEntity.getLngwgs()));
        geoipEntity.setLatitude(Float.parseFloat(aGeoipEntity.getLatwgs()));
        geoipEntity.setCountry(aGeoipEntity.getCountry());
        geoipEntity.setCity(aGeoipEntity.getCity());
        geoipEntity.setAddress("");
        geoipEntity.setArea("");
        geoipEntity.setAreaId("");
        geoipEntity.setCityId(aGeoipEntity.getAdcode());
        geoipEntity.setCountryId(aGeoipEntity.getAreacode());
        geoipEntity.setCounty("");
        geoipEntity.setCountyId(aGeoipEntity.getAdcode());
        geoipEntity.setDesc("");
        geoipEntity.setExtra1(aGeoipEntity.getSource());
        geoipEntity.setExtra2(aGeoipEntity.getOwner());
        geoipEntity.setIp(aGeoipEntity.getIp());
        geoipEntity.setIsp(aGeoipEntity.getIsp());
        geoipEntity.setIspId("");
        geoipEntity.setLip(0L);
        geoipEntity.setProvince(aGeoipEntity.getProvince());
        geoipEntity.setProvinceId(aGeoipEntity.getAdcode());
        geoipEntity.setType("");

        return geoipEntity;
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
        geoipEntity.setExtra1(gpsInfoDTO.getExtra1());
        geoipEntity.setExtra2(gpsInfoDTO.getExtra2());
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
