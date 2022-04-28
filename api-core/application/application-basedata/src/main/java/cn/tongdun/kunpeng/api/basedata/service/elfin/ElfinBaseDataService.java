package cn.tongdun.kunpeng.api.basedata.service.elfin;

import cn.fraudmetrix.elfin.biz.entity.PhoneAttrEntity;
import cn.fraudmetrix.elfin.biz.intf.BaseDataQueryService;
import cn.fraudmetrix.module.riskbase.object.MobileInfoDO;
import cn.tongdun.kunpeng.api.basedata.BasedataConstant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.GeoipEntity;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 归属地查询
 */
@Service
public class ElfinBaseDataService {
    private final static Logger logger = LoggerFactory.getLogger(ElfinBaseDataService.class);

    @Autowired
    private BaseDataQueryService baseDataQueryService;

    public GeoipEntity getIpInfo(String ip, AbstractFraudContext context) {
        try {
            if (StringUtils.isBlank(ip)) {
                return null;
            }
            //step1 从上下文取
            GeoipEntity geoipEntity = context.getExternalReturnObj(BasedataConstant.EXTERNAL_OBJ_GEOIP_ENTITY, GeoipEntity.class);
            if (geoipEntity != null && ip.equalsIgnoreCase(geoipEntity.getIp())) {
                return geoipEntity;
            }
            //step2 调用elfin获取
            cn.fraudmetrix.elfin.biz.entity.GeoipEntity elfinGeoipEntity = baseDataQueryService.getIpInfo(ip);
            if (elfinGeoipEntity != null) {
                return copyFromElfin(elfinGeoipEntity);
            }
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "ip归属地查询异常: {}", ip, e);
        }
        return null;
    }

    private GeoipEntity copyFromElfin(cn.fraudmetrix.elfin.biz.entity.GeoipEntity elfinGeoipEntity) {
        GeoipEntity geoipEntity = new GeoipEntity();
        elfinGeoipEntity.setIsp(geoipEntity.getIsp());
        elfinGeoipEntity.setCity(geoipEntity.getCity());
        elfinGeoipEntity.setProvince(geoipEntity.getProvince());
        elfinGeoipEntity.setAddress(geoipEntity.getAddress());
        elfinGeoipEntity.setArea(geoipEntity.getArea());
        elfinGeoipEntity.setAreaId(geoipEntity.getAreaId());
        elfinGeoipEntity.setCityId(geoipEntity.getCityId());
        elfinGeoipEntity.setCountryId(geoipEntity.getCountryId());
        elfinGeoipEntity.setCountry(geoipEntity.getCountry());
        elfinGeoipEntity.setDesc(geoipEntity.getDesc());
        elfinGeoipEntity.setType(geoipEntity.getType());
        elfinGeoipEntity.setProvinceId(geoipEntity.getProvinceId());
        elfinGeoipEntity.setIspId(geoipEntity.getIspId());
        elfinGeoipEntity.setLongitude(geoipEntity.getLongitude());
        elfinGeoipEntity.setLatitude(geoipEntity.getLatitude());
        elfinGeoipEntity.setLip(geoipEntity.getLip());
        elfinGeoipEntity.setIp(geoipEntity.getIp());
        elfinGeoipEntity.setExtra1(geoipEntity.getExtra1());
        elfinGeoipEntity.setExtra2(geoipEntity.getExtra2());
        elfinGeoipEntity.setCounty(geoipEntity.getCounty());
        elfinGeoipEntity.setCountyId(geoipEntity.getCountyId());
        return geoipEntity;
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
            //step1 从上下文取
            PhoneAttrEntity phoneAttrEntity = context.getPhoneAttrEntity(phone);
            if (phoneAttrEntity != null) {
                return phoneAttrEntity;
            }
            //step2 调用elfin获取
            phoneAttrEntity = baseDataQueryService.getPhoneInfo(phone);
            if (phoneAttrEntity != null) {
                context.setPhoneAttrEntity(phone, phoneAttrEntity);
                return phoneAttrEntity;
            }
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "手机归属地查询异常: {}", phone, e);
        }
        return null;
    }


    /**
     * TODO 过度阶段老方法，后续线上验证后可删除 start
     */

    private Map<String, cn.fraudmetrix.module.riskbase.geoip.GeoipEntity> geoipEntitycache = new ConcurrentHashMap<>();
    private Map<String, PhoneAttrEntity> phoneAttrEntityCache = new ConcurrentHashMap<>();

    public cn.fraudmetrix.module.riskbase.geoip.GeoipEntity getIpInfo(String ip) {
        try {
            if (StringUtils.isBlank(ip)) {
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
            logger.warn(TraceUtils.getFormatTrace() + "ip归属地查询异常: {}", ip, e);
        }
        return null;
    }


    public PhoneAttrEntity getPhoneInfo(String phone) {
        try {
            if (StringUtils.isBlank(phone)) {
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
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "手机归属地查询异常: {}", phone, e);
        }
        return null;
    }

    public MobileInfoDO getMobileInfo(String phone) {
        if (StringUtils.isBlank(phone)) {
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