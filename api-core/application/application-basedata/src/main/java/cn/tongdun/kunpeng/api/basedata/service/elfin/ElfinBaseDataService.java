package cn.tongdun.kunpeng.api.basedata.service.elfin;

import cn.fraudmetrix.elfin.biz.entity.PhoneAttrEntity;
import cn.fraudmetrix.elfin.biz.intf.BaseDataQueryService;
import cn.tongdun.evan.client.dubbo.AGeoipInfoQueryService;
import cn.tongdun.evan.client.entity.AGeoipEntity;
import cn.tongdun.evan.client.entity.AGeoipQueryDTO;
import cn.tongdun.evan.client.lang.Result;
import cn.tongdun.kunpeng.api.basedata.BasedataConstant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.GeoipEntity;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by coco on 17/12/26.
 */
@Service
public class ElfinBaseDataService {
    private final static Logger logger = LoggerFactory.getLogger(ElfinBaseDataService.class);

    @Autowired
    private BaseDataQueryService baseDataQueryService;

    @Autowired
    private AGeoipInfoQueryService aGeoipInfoQueryService;

    @Value("${app.name:kunpeng-api}")
    private String appName;

    public GeoipEntity getIpInfo(String ip, AbstractFraudContext context) {
        try {
            if (StringUtils.isBlank(ip)) {
                return null;
            }
            GeoipEntity geoipEntity = context.getExternalReturnObj(BasedataConstant.EXTERNAL_OBJ_GEOIP_ENTITY, GeoipEntity.class);
            if (geoipEntity != null && ip.equalsIgnoreCase(geoipEntity.getIp())) {
                return geoipEntity;
            }
            AGeoipQueryDTO queryDTO = new AGeoipQueryDTO();
            queryDTO.setIp(ip);
            queryDTO.setPartnerCode(context.getPartnerCode());
            queryDTO.setSeqId(context.getSeqId());
            queryDTO.setSource(appName);
            Result<AGeoipEntity> aGeoipEntity = aGeoipInfoQueryService.queryGeoipInfo(queryDTO);
            if (aGeoipEntity == null) {
                logger.warn("ElfinBaseDataService::getIpInfo by evan return null, ip: {}", ip);
                return null;
            }
            if (aGeoipEntity.isSuccess() && "200".equalsIgnoreCase(aGeoipEntity.getCode())) {
                return copyGeoipEntityProperties(aGeoipEntity.getData());
            }
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "ElfinBaseDataService::getIpInfo by evan error, ip: {}", ip, e);
        }
        return null;
    }

    private GeoipEntity copyGeoipEntityProperties(AGeoipEntity geoipEntity) {
        if (geoipEntity == null) {
            return null;
        }
        GeoipEntity copy = new GeoipEntity();
        copy.setIp(geoipEntity.getIp());
        copy.setIsp(geoipEntity.getIsp());
        copy.setProvince(geoipEntity.getProvince());
        copy.setCity(geoipEntity.getCity());
        copy.setCountry(geoipEntity.getCountry());
        copy.setArea(geoipEntity.getAreacode());
        copy.setIspId(geoipEntity.getIsp());
        copy.setLongitude(StringUtils.isEmpty(geoipEntity.getLngwgs()) ? 0.0f : Float.parseFloat(geoipEntity.getLngwgs()));
        copy.setLatitude(StringUtils.isEmpty(geoipEntity.getLatwgs()) ? 0.0f : Float.parseFloat(geoipEntity.getLatwgs()));

        copy.setCounty(geoipEntity.getDistrict());
        return copy;
    }

    public PhoneAttrEntity getMobileInfo(String phone, AbstractFraudContext context) {
        if (StringUtils.isBlank(phone)) {
            return null;
        }
        return getPhoneInfo(phone, context);
    }

    public PhoneAttrEntity getPhoneInfo(String phone, AbstractFraudContext context) {
        try {
            if (StringUtils.isBlank(phone)) {
                return null;
            }

            PhoneAttrEntity phoneAttrEntity = context.getPhoneAttrEntity(phone);
            if (phoneAttrEntity != null) {
                return phoneAttrEntity;
            }
            phoneAttrEntity = baseDataQueryService.getPhoneInfo(phone);
            if (phoneAttrEntity == null) {
                logger.error(TraceUtils.getFormatTrace() + "ElfinBaseDataService::getPhoneInfo by elfin return null, phone: {}", phone);
                return null;
            }
            context.setPhoneAttrEntity(phone, phoneAttrEntity);
            return phoneAttrEntity;
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "ElfinBaseDataService::getPhoneInfo by elfin error, phone: {}", phone, e);
            return null;
        }
    }


}