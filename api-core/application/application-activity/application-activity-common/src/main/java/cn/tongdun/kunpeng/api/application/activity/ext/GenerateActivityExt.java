package cn.tongdun.kunpeng.api.application.activity.ext;

import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.tongdun.kunpeng.api.application.activity.common.ActitivyMsg;
import cn.tongdun.kunpeng.api.application.activity.common.GeoipInfo;
import cn.tongdun.kunpeng.api.application.activity.common.IActitivyMsg;
import cn.tongdun.kunpeng.api.application.activity.common.IGenerateActivityExtPt;
import cn.tongdun.kunpeng.api.basedata.BasedataConstant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.QueueItem;
import cn.tongdun.tdframework.core.extension.Extension;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: liang.chen
 * @Date: 2020/3/4 下午4:00
 */
@Extension(business = BizScenario.DEFAULT, tenant = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class GenerateActivityExt implements IGenerateActivityExtPt {

    private static final Logger logger = LoggerFactory.getLogger(GenerateActivityExt.class);

    private Map<String, Method> systemFieldGetter;


    /**
     * 根据出入参、上下文生成Activity消息
     *
     * @param queueItem
     * @return
     */
    @Override
    public IActitivyMsg generateActivity(QueueItem queueItem) {
        AbstractFraudContext context = queueItem.getContext();

        ActitivyMsg actitivy = new ActitivyMsg();
        actitivy.setProduceTime(System.currentTimeMillis());
        actitivy.setSequenceId(context.getSeqId());

        actitivy.setRequest(encodeRequest(queueItem));
        actitivy.setResponse(queueItem.getResponse());
        actitivy.setSubReasonCodes(context.getSubReasonCodes());
        actitivy.setGeoipEntity(getGeoIpInfo(queueItem.getContext()));

        Map deviceInfo = context.getDeviceInfo();
        //详情使用，复制一份出来，避免输出到客户响应中（这里是异步执行的，有可能会影响到response中的deviceInfo）
        Map<String, Object> deviceInfoMap = new HashMap<>();
        deviceInfoMap.putAll(deviceInfo);
        deviceInfoMap.put("blackBox", queueItem.getRequest().getBlackBox());
        actitivy.setDeviceInfo(deviceInfoMap);

//        logger.info("GenerateActivityExt....................seqId={}, actitivy={}", context.getSeqId(), JSON.toJSONString(actitivy));

        return actitivy;
    }


    /**
     * 提取FraudContext中所有的系统字段和扩展字段
     */
    private Map encodeRequest(QueueItem queueItem) {
        AbstractFraudContext context = queueItem.getContext();

        // 额外添加status
        String status = queueItem.getResponse().getFinalDecision();

        //取得上下文中基础的字段
        Map result = getBaseField(context);
        result.put("status", status);

        // 获取字段值
        Map<String, Object> fieldValues = context.getFieldValues();
        putAllIfNotExists(result, fieldValues);
        result.put("eventRealTime", new Date());

        return result;
    }

    private Map getBaseField(AbstractFraudContext context) {
        Map result = new HashMap();
        result.put("partnerCode", context.getPartnerCode());
        result.put("sequenceId", context.getSeqId());
        result.put("appName", context.getAppName());
        result.put("appType", context.getAppType());
        result.put("eventId", context.getEventId());
        result.put("eventType", context.getEventType());
        result.put("policyUuid", context.getPolicyUuid());
        result.put("eventOccurTime", context.getEventOccurTime());
        result.put("policyVersion", context.getPolicyVersion());
        result.put("requestId", context.getRequestId());
        if (StringUtils.isNotBlank(context.getChallengerTag())) {
            result.put("isChallenger", context.isChallenger());
            result.put("challengerTag", context.getChallengerTag());
        }
        Object originalSeqId = context.getFieldValues().get("originalSeqId");
        if (Objects.nonNull(originalSeqId)) {
            result.put("challengerType", "copy");
        }
        return result;
    }

    private GeoipInfo getGeoIpInfo(AbstractFraudContext context) {
        GeoipEntity geoipEntity = context.getExternalReturnObj(BasedataConstant.EXTERNAL_OBJ_GEOIP_ENTITY, GeoipEntity.class);
        if (geoipEntity != null) {
            GeoipInfo geoipInfo = new GeoipInfo();
            String country = geoipEntity.getCountry();
            try {
                if (StringUtils.contains(country, "香港")
                        || StringUtils.contains(country, "澳门")
                        || StringUtils.contains(country, "台湾")) {
                    country = "中国";
                }
            } catch (Exception e) {
                logger.warn("国家转换异常, seq_id: {}", context.getSeqId(), e);
            }
            geoipInfo.setCountry(country);
            geoipInfo.setCountyId(geoipEntity.getCountyId());
            geoipInfo.setProvince(geoipEntity.getProvince());
            geoipInfo.setProvinceId(geoipEntity.getProvinceId());
            geoipInfo.setCity(geoipEntity.getCity());
            geoipInfo.setCityId(geoipEntity.getCityId());
            geoipInfo.setCounty(geoipEntity.getCounty());
            geoipInfo.setCountyId(geoipEntity.getCountyId());
            geoipInfo.setStreet(geoipEntity.getAddress());
            geoipInfo.setLatitude(geoipEntity.getLatitude());
            geoipInfo.setLongitude(geoipEntity.getLongitude());
            geoipInfo.setIsp(geoipEntity.getIsp());
            return geoipInfo;
        }
        return null;
    }

    private void putAllIfNotExists(Map<String, Object> result, Map<String, Object> map) {
        for (String fieldName : map.keySet()) {
            if (result.containsKey(fieldName)) {
                continue;
            }
            Object fieldValue = map.get(fieldName);
            if (fieldValue == null) {
                continue;
            }

            result.put(fieldName, fieldValue);
        }
    }
}
