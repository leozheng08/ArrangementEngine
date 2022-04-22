package cn.tongdun.kunpeng.api.basedata.service.elfin;

import cn.fraudmetrix.elfin.biz.entity.PhoneAttrEntity;
import cn.fraudmetrix.elfin.biz.intf.BaseDataQueryService;
import cn.fraudmetrix.module.riskbase.object.MobileInfoDO;
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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


    /**
     * TODO 过度阶段老方法，后续线上验证后可删除 start
     */

    private Map<String, cn.fraudmetrix.module.riskbase.geoip.GeoipEntity> geoipEntitycache = new ConcurrentHashMap<>();
    private Map<String, PhoneAttrEntity> phoneAttrEntityCache = new ConcurrentHashMap<>();

    public cn.fraudmetrix.module.riskbase.geoip.GeoipEntity getIpInfo(String ip) {
        try {
            if (StringUtils.isBlank(ip)) {
                logger.info(TraceUtils.getFormatTrace() + "elfinBaseDateService get geoentity from elfin with params null");
                return null;
            }
            cn.fraudmetrix.module.riskbase.geoip.GeoipEntity riskbaseGeoipEntity = geoipEntitycache.get(ip);
            if (riskbaseGeoipEntity != null) {
                return riskbaseGeoipEntity;
            }

            cn.fraudmetrix.elfin.biz.entity.GeoipEntity geoipEntity = baseDataQueryService.getIpInfo(ip);
            if (geoipEntity != null) {
                riskbaseGeoipEntity = copyGeoipEntityProperties(geoipEntity);
                geoipEntitycache.put(ip, riskbaseGeoipEntity);
                return riskbaseGeoipEntity;
            }
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "ip query geoinfo failed ip {}", ip, e);
        }
        return null;
    }


    public PhoneAttrEntity getPhoneInfo(String phone) {
        try {
            if (StringUtils.isBlank(phone)) {
                logger.info(TraceUtils.getFormatTrace() + "elfinBaseDateService get phoneInfo from elfin with params null");
                return null;
            }

            PhoneAttrEntity phoneAttrEntity = phoneAttrEntityCache.get(phone);
            if (phoneAttrEntity != null) {
                return phoneAttrEntity;
            }

            phoneAttrEntity = baseDataQueryService.getPhoneInfo(phone);
            if (phoneAttrEntity != null) {
                phoneAttrEntityCache.put(phone, phoneAttrEntity);
                return phoneAttrEntity;
            }
            return null;
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "phone query detailinfo error phone {}", phone, e);
            return null;
        }
    }

    public MobileInfoDO getMobileInfo(String phone) {
        if (StringUtils.isBlank(phone)) {
            logger.info(TraceUtils.getFormatTrace() + "elfinBaseDateService get phoneInfo from elfin with params null");
            return null;
        }
        PhoneAttrEntity phoneAttrEntity = getPhoneInfo(phone);
        if (phoneAttrEntity != null) {
            return copyMobileInofProperties(phoneAttrEntity);
        }
        return null;
    }

    private cn.fraudmetrix.module.riskbase.geoip.GeoipEntity copyGeoipEntityProperties(cn.fraudmetrix.elfin.biz.entity.GeoipEntity geoipEntity) {
        //springbeanutils反射有性能问题
        cn.fraudmetrix.module.riskbase.geoip.GeoipEntity oldGeoipEntity = new cn.fraudmetrix.module.riskbase.geoip.GeoipEntity();
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

    private MobileInfoDO copyMobileInofProperties(PhoneAttrEntity phoneAttrEntity) {
        MobileInfoDO mobileInfoDO = new MobileInfoDO();
        mobileInfoDO.setCity(phoneAttrEntity.getCity());
        mobileInfoDO.setProvince(phoneAttrEntity.getProvince());
        mobileInfoDO.setPhoneNumber(phoneAttrEntity.getPhonePrefix());
        if (StringUtils.isNoneBlank(phoneAttrEntity.getType())) {
            try {
                mobileInfoDO.setType(Integer.valueOf(phoneAttrEntity.getType()));
            } catch (NumberFormatException e) {
            }
        }
        return mobileInfoDO;
    }

    /**
     * TODO 过度阶段老方法，后续线上验证后可删除 end
     */

}