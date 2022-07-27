package cn.tongdun.kunpeng.api.basedata.service;

import cn.tongdun.evan.client.dubbo.AGeoipInfoQueryService;
import cn.tongdun.evan.client.entity.AGeoipEntity;
import cn.tongdun.evan.client.entity.AGeoipQueryDTO;
import cn.tongdun.evan.client.lang.Result;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.GeoipEntity;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.extension.Extension;
import cn.tongdun.tdframework.core.metrics.IMetrics;
import cn.tongdun.tdframework.core.metrics.ITimeContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:02 下午
 */
@Extension(tenant = "us", business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class UsGeoIpService implements GeoIpServiceExtPt {

    private static final Logger logger = LoggerFactory.getLogger(UsGeoIpService.class);

    @Autowired
    private AGeoipInfoQueryService aGeoipInfoQueryService;

    @Autowired
    private IMetrics metrics;


    @Override
    public GeoipEntity getIpInfo(String ip, AbstractFraudContext context) {
        if (StringUtils.isBlank(ip)) {
            return null;
        }
        Result<AGeoipEntity> result = null;
        AGeoipQueryDTO geoipQueryDTO = new AGeoipQueryDTO();
        geoipQueryDTO.setIp(ip);
        geoipQueryDTO.setPartnerCode(context.getPartnerCode());
        geoipQueryDTO.setSource("evan-us");
        geoipQueryDTO.setSeqId(context.getSeqId());
        try {
            String[] tags = {
                    "dubbo_qps", "paas.api.geoip"};
            metrics.counter("kunpeng.api.dubbo.qps", tags);
            ITimeContext timeContext = metrics.metricTimer("kunpeng.api.dubbo.rt", tags);

            String[] partnerTags = {
                    "partner_code", context.getPartnerCode()};
            ITimeContext timePartner = metrics.metricTimer("kunpeng.api.dubbo.partner.rt", partnerTags);

            result = aGeoipInfoQueryService.queryGeoipInfo(geoipQueryDTO);
            timeContext.stop();
            timePartner.stop();

        } catch (Exception e) {
            if (ReasonCodeUtil.isTimeout(e)) {
                ReasonCodeUtil.add(context, ReasonCode.GEOIP_SERVICE_CALL_TIMEOUT, "geoIp-us");
                logger.error(TraceUtils.getFormatTrace() + "UsGeoIpService gpsQueryService.queryGps error:" + JSON.toJSONString(result), e);
            } else {
                ReasonCodeUtil.add(context, ReasonCode.GEOIP_SERVICE_CALL_ERROR, "geoIp-us");
                logger.error(TraceUtils.getFormatTrace() + "UsGeoIpService gpsQueryService.queryGps error:" + JSON.toJSONString(result), e);
            }

        }
        if (Objects.isNull(result)) {
            return null;
        }
        if (!result.isSuccess()) {
            logger.info("aGeoipInfoQueryService.queryGeoipInfo:{}", JSON.toJSONString(result));
            switch (result.getCode()) {
                case "100":
                    logger.info("GEOIP_PARAM_ERROR入参:{}", JSON.toJSONString(geoipQueryDTO));
                    ReasonCodeUtil.add(context, ReasonCode.GEOIP_PARAM_ERROR, "geoIp-us");
                    return null;
                case "102":
                    logger.info("GEOIP_ILLEGAL_ERROR入参:{}", JSON.toJSONString(geoipQueryDTO));
                    ReasonCodeUtil.add(context, ReasonCode.GEOIP_ILLEGAL_ERROR, "geoIp-us");
                    return null;
                case "403":
                    logger.info("GEOIP_PERNISSION_ERROR入参:{}", JSON.toJSONString(geoipQueryDTO));
                    ReasonCodeUtil.add(context, ReasonCode.GEOIP_PERNISSION_ERROR, "geoIp-us");
                    return null;
                case "500":
                    logger.info("GEOIP_SERRVER_ERROR入参:{}", JSON.toJSONString(geoipQueryDTO));
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
        try {
            if (StringUtils.isNotEmpty(aGeoipEntity.getLngwgs())) {
                geoipEntity.setLongitude(Float.parseFloat(aGeoipEntity.getLngwgs()));
            }
            if (StringUtils.isNotEmpty(aGeoipEntity.getLatwgs())) {
                geoipEntity.setLatitude(Float.parseFloat(aGeoipEntity.getLatwgs()));
            }
        } catch (Exception e) {
            logger.error("UsGeoIpService fromDTO2EntityV2 error:", e);
        }
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

}
