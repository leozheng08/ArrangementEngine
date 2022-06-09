package cn.tongdun.kunpeng.api.basedata.service.elfin;

import cn.fraudmetrix.elfin.biz.entity.PhoneAttrEntity;
import cn.fraudmetrix.elfin.biz.intf.BaseDataQueryService;
import cn.tongdun.kunpeng.api.basedata.BasedataConstant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.GeoipEntity;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 归属地查询
 */
@Service
public class ElfinBaseDataService {
    private final static Logger logger = LoggerFactory.getLogger(ElfinBaseDataService.class);

    @Autowired
    private BaseDataQueryService baseDataQueryService;

    /**
     * 获取ip归属地
     */
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
            if (ReasonCodeUtil.isTimeout(e)) {
                logger.warn(TraceUtils.getFormatTrace() + "调用ip归属地服务超时: {}", ip, e);
            } else {
                logger.error(TraceUtils.getFormatTrace() + "调用ip归属地服务异常: {}", ip, e);
            }
        }
        return null;
    }

    private GeoipEntity copyFromElfin(cn.fraudmetrix.elfin.biz.entity.GeoipEntity elfinGeoipEntity) {
        GeoipEntity geoipEntity = new GeoipEntity();
        geoipEntity.setIsp(elfinGeoipEntity.getIsp());
        geoipEntity.setCity(elfinGeoipEntity.getCity());
        geoipEntity.setProvince(elfinGeoipEntity.getProvince());
        geoipEntity.setAddress(elfinGeoipEntity.getAddress());
        geoipEntity.setArea(elfinGeoipEntity.getArea());
        geoipEntity.setAreaId(elfinGeoipEntity.getAreaId());
        geoipEntity.setCityId(elfinGeoipEntity.getCityId());
        geoipEntity.setCountryId(elfinGeoipEntity.getCountryId());
        geoipEntity.setCountry(elfinGeoipEntity.getCountry());
        geoipEntity.setDesc(elfinGeoipEntity.getDesc());
        geoipEntity.setType(elfinGeoipEntity.getType());
        geoipEntity.setProvinceId(elfinGeoipEntity.getProvinceId());
        geoipEntity.setIspId(elfinGeoipEntity.getIspId());
        geoipEntity.setLongitude(elfinGeoipEntity.getLongitude());
        geoipEntity.setLatitude(elfinGeoipEntity.getLatitude());
        geoipEntity.setLip(elfinGeoipEntity.getLip());
        geoipEntity.setIp(elfinGeoipEntity.getIp());
        geoipEntity.setExtra1(elfinGeoipEntity.getExtra1());
        geoipEntity.setExtra2(elfinGeoipEntity.getExtra2());
        geoipEntity.setCounty(elfinGeoipEntity.getCounty());
        geoipEntity.setCountyId(elfinGeoipEntity.getCountyId());
        return geoipEntity;
    }

    /**
     * 获取手机归属地
     */
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
            if (ReasonCodeUtil.isTimeout(e)) {
                logger.warn(TraceUtils.getFormatTrace() + "调用手机归属地服务超时: {}", phone, e);
            } else {
                logger.error(TraceUtils.getFormatTrace() + "调用手机归属地服务异常: {}", phone, e);
            }
        }
        return null;
    }


}