//package cn.tongdun.kunpeng.api.basedata.service;
//
//import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
//import cn.tongdun.evan.client.dubbo.AGeoipInfoQueryService;
//import cn.tongdun.evan.client.entity.AGeoipEntity;
//import cn.tongdun.evan.client.entity.AGeoipQueryDTO;
//import cn.tongdun.evan.client.lang.Result;
//import cn.tongdun.kunpeng.share.utils.TraceUtils;
//import cn.tongdun.tdframework.core.extension.BizScenario;
//import cn.tongdun.tdframework.core.extension.Extension;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @author: yuanhang
// * @date: 2021-03-08 16:47
// **/
//@Extension(tenant = "sea", business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
//public class SeaGeoIpService implements GeoIpServiceExtPt{
//
//    private Logger logger = LoggerFactory.getLogger(SeaGeoIpService.class);
//
//    @Autowired
//    private AGeoipInfoQueryService evanQueryService;
//
//    @Override
//    public GeoipEntity getIpInfo(String ip) {
//        try {
//            if (StringUtils.isBlank(ip)) {
//                logger.info(TraceUtils.getFormatTrace() + "elfinBaseDateService get geoentity from elfin with params null");
//                return null;
//            }
//            AGeoipQueryDTO queryDTO = new AGeoipQueryDTO();
//            queryDTO.setIp(ip);
//            Result<AGeoipEntity> aGeoipEntity = evanQueryService.queryGeoipInfo(queryDTO);
//            return copyGeoipEntityProperties(aGeoipEntity.getData());
//        } catch (Exception e) {
//            logger.error(TraceUtils.getFormatTrace() + "ip query geoinfo failed ip {}", ip, e);
//        }
//        return null;
//    }
//
//    private GeoipEntity copyGeoipEntityProperties(AGeoipEntity geoipEntity) {
//        //springbeanutils反射有性能问题
//        GeoipEntity oldGeoipEntity = new GeoipEntity();
//        if (geoipEntity == null) {
//            return null;
//        }
//        oldGeoipEntity.setIsp(geoipEntity.getIsp());
//        oldGeoipEntity.setCity(geoipEntity.getCity());
//        oldGeoipEntity.setProvince(geoipEntity.getProvince());
////        oldGeoipEntity.setAddress(geoipEntity.getAddress());
//        oldGeoipEntity.setArea(geoipEntity.getAreacode());
////        oldGeoipEntity.setAreaId(geoipEntity.getAreacode());
////        oldGeoipEntity.setCityId(geoipEntity.getCityId());
////        oldGeoipEntity.setCountryId(geoipEntity.getCountryId());
//        oldGeoipEntity.setCountry(geoipEntity.getCountry());
////        oldGeoipEntity.setDesc(geoipEntity.getDesc());
////        oldGeoipEntity.setType(geoipEntity.getType());
////        oldGeoipEntity.setProvinceId(geoipEntity.getProvinceId());
//        oldGeoipEntity.setIspId(geoipEntity.getIsp());
//        oldGeoipEntity.setLongitude(Float.valueOf(geoipEntity.getLngwgs()));
//        oldGeoipEntity.setLatitude(Float.valueOf(geoipEntity.getLatwgs()));
////        oldGeoipEntity.setLip(geoipEntity.getLip());
//        oldGeoipEntity.setIp(geoipEntity.getIp());
////        oldGeoipEntity.setExtra1(geoipEntity.getExtra1());
////        oldGeoipEntity.setExtra2(geoipEntity.getExtra2());
//        oldGeoipEntity.setCounty(geoipEntity.getDistrict());
////        oldGeoipEntity.setCountyId(geoipEntity.getCountyId());
//        return oldGeoipEntity;
//    }
//
//    @Override
//    public Map<String, GeoipEntity> batchGetIpInfo(List<String> ipList) {
//        return null;
//    }
//}
