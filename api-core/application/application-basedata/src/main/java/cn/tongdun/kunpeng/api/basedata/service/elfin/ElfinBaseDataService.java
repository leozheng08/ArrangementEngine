package cn.tongdun.kunpeng.api.basedata.service.elfin;

import cn.fraudmetrix.elfin.biz.entity.IdcEntity;
import cn.fraudmetrix.elfin.biz.entity.PhoneAttrEntity;
import cn.fraudmetrix.elfin.biz.entity.StationEntity;
import cn.fraudmetrix.elfin.biz.intf.BaseDataQueryService;
import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.fraudmetrix.module.riskbase.object.MobileInfoDO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by coco on 17/12/26.
 */
@Service
public class ElfinBaseDataService {
    private final static Logger logger = LoggerFactory.getLogger(ElfinBaseDataService.class);

    @Autowired
    private BaseDataQueryService baseDataQueryService;

    // FIXME: 2/7/20 cache handle
    private Map<String, GeoipEntity> geoipEntitycache = new ConcurrentHashMap<>();
    private Map<String, PhoneAttrEntity> phoneAttrEntityCache = new ConcurrentHashMap<>();


    public GeoipEntity getIpInfo(String ip) {
        try {
            if (StringUtils.isBlank(ip)) {
                logger.info(TraceUtils.getFormatTrace()+"elfinBaseDateService get geoentity from elfin with params null");
                return null;
            }
            GeoipEntity riskbaseGeoipEntity = geoipEntitycache.get(ip);
            if (riskbaseGeoipEntity != null) {
                return riskbaseGeoipEntity;
            }

            cn.fraudmetrix.elfin.biz.entity.GeoipEntity geoipEntity = baseDataQueryService.getIpInfo(ip);
            if (geoipEntity != null) {
                riskbaseGeoipEntity = copyGeoipEntityProperties(geoipEntity);
                geoipEntitycache.put(ip, riskbaseGeoipEntity);
                return riskbaseGeoipEntity;
            }
        }
        catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace()+"ip query geoinfo failed ip {}", ip, e);
        }
        return null;
    }

    public Map<String, GeoipEntity> getIpInfos(List<String> ips) {
        if (CollectionUtils.isEmpty(ips)) {
            logger.warn(TraceUtils.getFormatTrace()+"getIpInfos from elfin with params empty");
            return Collections.emptyMap();
        }

        List<String> inCacheIpList = new ArrayList<>();
        Map<String, GeoipEntity> inCacheMap = new HashMap<>();
        List<String> notInCacheIpList = new ArrayList<>();

        for (String ip : ips) {
            GeoipEntity riskbaseGeoipEntity = geoipEntitycache.get(ip);
            if (riskbaseGeoipEntity != null) {
                inCacheIpList.add(ip);
                inCacheMap.put(ip, riskbaseGeoipEntity);
            }
            else {
                notInCacheIpList.add(ip);
            }
        }

        if (CollectionUtils.isEmpty(notInCacheIpList)) {
            return inCacheMap;
        }

        final Map<String, GeoipEntity> finalResultMap = Maps.newHashMapWithExpectedSize(ips.size());
        if (MapUtils.isNotEmpty(inCacheMap)) {
            finalResultMap.putAll(inCacheMap);
        }

        Map<String, cn.fraudmetrix.elfin.biz.entity.GeoipEntity> ipInfos = null;
        try {
            ipInfos = baseDataQueryService.getIpInfos(notInCacheIpList);
        }
        catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace()+"getIpInfos error, ipList={}", ips, e);
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
            geoipEntitycache.put(entry.getKey(), geoipEntity);
        }

        return finalResultMap;
    }

    public StationEntity getStationInfo(String ip) {
        try {
            if (StringUtils.isBlank(ip)) {
                logger.info(TraceUtils.getFormatTrace()+"elfinBaseDateService get stationEngity from elfin with params null");
                return null;
            }
            return baseDataQueryService.getStationInfo(ip);
        }
        catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace()+"ip query stationinfo error ip {}", ip, e);
            return null;
        }
    }

    public boolean isStation(String ip) {
        try {
            if (StringUtils.isBlank(ip)) {
                logger.info(TraceUtils.getFormatTrace()+"elfinBaseDateService get isStation from elfin with params null");
                return false;
            }
            return baseDataQueryService.isStation(ip);
        }
        catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace()+"ip check isstation error ip {}", ip, e);
            return false;
        }
    }

    public IdcEntity getIdcInfo(String ip) {
        try {
            if (StringUtils.isBlank(ip)) {
                logger.info(TraceUtils.getFormatTrace()+"elfinBaseDateService get idcEntity from elfin with params null");
                return null;
            }
            return baseDataQueryService.getIdcInfo(ip);
        }
        catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace()+"ip query idcinfo error ip {}", ip, e);

            return null;
        }
    }

    public boolean isIdc(String ip) {
        try {
            if (StringUtils.isBlank(ip)) {
                logger.info(TraceUtils.getFormatTrace()+"elfinBaseDateService get idc from elfin with params null");
                return false;
            }
            return baseDataQueryService.isIdc(ip);
        }
        catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace()+"ip check id idc error ip {}", ip, e);
            return false;
        }
    }

    public PhoneAttrEntity getPhoneInfo(String phone) {
        try {
            if (StringUtils.isBlank(phone)) {
                logger.info(TraceUtils.getFormatTrace()+"elfinBaseDateService get phoneInfo from elfin with params null");
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
        }
        catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace()+"phone query detailinfo error phone {}", phone, e);
            return null;
        }
    }

    public MobileInfoDO getMobileInfo(String phone) {
        if (StringUtils.isBlank(phone)) {
            logger.info(TraceUtils.getFormatTrace()+"elfinBaseDateService get phoneInfo from elfin with params null");
            return null;
        }
        PhoneAttrEntity phoneAttrEntity = getPhoneInfo(phone);
        if (phoneAttrEntity != null) {
            return copyMobileInofProperties(phoneAttrEntity);
        }
        return null;
    }

    private GeoipEntity copyGeoipEntityProperties(cn.fraudmetrix.elfin.biz.entity.GeoipEntity geoipEntity) {
        //springbeanutils反射有性能问题
        GeoipEntity oldGeoipEntity = new GeoipEntity();
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
            }
            catch (NumberFormatException e) {
            }
        }
        return mobileInfoDO;
    }
}