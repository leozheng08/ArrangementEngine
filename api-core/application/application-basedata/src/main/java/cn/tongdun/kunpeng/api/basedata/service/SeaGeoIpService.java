package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.tongdun.evan.client.dubbo.AGeoipInfoQueryService;
import cn.tongdun.evan.client.entity.AGeoipEntity;
import cn.tongdun.evan.client.entity.AGeoipQueryDTO;
import cn.tongdun.evan.client.lang.Result;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.api.engine.model.dictionary.DictionaryManager;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.extension.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author: yuanhang
 * @date: 2021-03-08 16:47
 **/
@Extension(tenant = "sea", business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class SeaGeoIpService implements GeoIpServiceExtPt{

    private Logger logger = LoggerFactory.getLogger(SeaGeoIpService.class);

    @Autowired
    private AGeoipInfoQueryService evanQueryService;

    @Autowired
    private DictionaryManager dictionaryManager;

    @Override
    public GeoipEntity getIpInfo(String ip, AbstractFraudContext context) {
        try {
            if (StringUtils.isBlank(ip)) {
                logger.info(TraceUtils.getFormatTrace() + "AGeoipInfoQueryService get geoentity from evan with params null");
                return null;
            }
            AGeoipQueryDTO queryDTO = new AGeoipQueryDTO();
            queryDTO.setIp(ip);
            queryDTO.setPartnerCode(context.getPartnerCode());
            queryDTO.setSeqId(context.getSeqId());
            queryDTO.setSource("kunpeng-sea-api");
            Result<AGeoipEntity> aGeoipEntity = evanQueryService.queryGeoipInfo(queryDTO);
            if (aGeoipEntity.isSuccess() && "200".equalsIgnoreCase(aGeoipEntity.getCode())) {
                return copyGeoipEntityProperties(aGeoipEntity.getData());
            }

            // refs: http://wiki.tongdun.me/pages/viewpage.action?pageId=37815128
            logger.error(TraceUtils.getFormatTrace() + "ip query geoInfo failed ip {}, aGeoip code :{}, msg :{}", ip, aGeoipEntity.getCode(), aGeoipEntity.getMsg());
            String subReasonCode = dictionaryManager.getReasonCode("geoIp", aGeoipEntity.getCode());
            // 针对字典表中未配置的状态子码，暂时不做处理
            if (StringUtils.isNotEmpty(subReasonCode)) {
                String subReasonCodeMessage = dictionaryManager.getMessage(subReasonCode);
                ReasonCodeUtil.addExtCode(context, subReasonCode, subReasonCodeMessage, "geoip", "queryGeoipInfo", aGeoipEntity.getCode(), aGeoipEntity.getMsg());
            }
            return null;
        } catch (Exception e) {
            if (ReasonCodeUtil.isTimeout(e)) {
                ReasonCodeUtil.add(context, ReasonCode.GEOIP_SERVICE_CALL_TIMEOUT, "geoip");
            } else {
                ReasonCodeUtil.add(context, ReasonCode.GEOIP_SERVICE_CALL_ERROR, "geoip");
            }
            logger.error(TraceUtils.getFormatTrace() + "ip query geoinfo failed ip" + ip, e);
        }
        return null;
    }

    private GeoipEntity copyGeoipEntityProperties(AGeoipEntity geoipEntity) {
        GeoipEntity oldGeoipEntity = new GeoipEntity();
        if (geoipEntity == null) {
            return null;
        }
        oldGeoipEntity.setIsp(geoipEntity.getIsp());
        oldGeoipEntity.setCity(geoipEntity.getCity());
        oldGeoipEntity.setProvince(geoipEntity.getProvince());
        oldGeoipEntity.setArea(geoipEntity.getAreacode());
        oldGeoipEntity.setCountry(geoipEntity.getCountry());
        oldGeoipEntity.setIspId(geoipEntity.getIsp());
        oldGeoipEntity.setLongitude(Float.valueOf(geoipEntity.getLngwgs()));
        oldGeoipEntity.setLatitude(Float.valueOf(geoipEntity.getLatwgs()));
        oldGeoipEntity.setIp(geoipEntity.getIp());
        oldGeoipEntity.setCounty(geoipEntity.getDistrict());
        return oldGeoipEntity;
    }

    /**
     * evan 暂无batch接口
     */
    @Override
    public Map<String, GeoipEntity> batchGetIpInfo(List<String> ipList) {
        return null;
    }
}
