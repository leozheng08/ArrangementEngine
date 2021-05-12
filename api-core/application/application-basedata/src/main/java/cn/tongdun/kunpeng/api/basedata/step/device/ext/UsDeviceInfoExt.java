package cn.tongdun.kunpeng.api.basedata.step.device.ext;

import cn.fraudmetrix.forseti.fp.dubbo.DeviceInfoQuery;
import cn.fraudmetrix.forseti.fp.model.BaseResult;
import cn.fraudmetrix.forseti.fp.model.DeviceResp;
import cn.fraudmetrix.forseti.fp.model.QueryParams;
import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.tongdun.kunpeng.api.basedata.constant.FpReasonCodeEnum;
import cn.tongdun.kunpeng.api.basedata.constant.RespDetailTypeEnum;
import cn.tongdun.kunpeng.api.basedata.service.GeoIpServiceExtPt;
import cn.tongdun.kunpeng.api.basedata.util.FpReasonUtils;
import cn.tongdun.kunpeng.api.common.MetricsConstant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.api.engine.model.rule.util.DataUtil;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.extension.Extension;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.metrics.IMetrics;
import cn.tongdun.tdframework.core.metrics.ITimeContext;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 北美设备指纹获取实现
 * @author jie
 * @date 2021/1/20
 */
@Extension(business = BizScenario.DEFAULT,tenant = "us",partner = BizScenario.DEFAULT)
public class UsDeviceInfoExt implements DeviceInfoExtPt{

    private static final Logger logger = LoggerFactory.getLogger(UsDeviceInfoExt.class);

    @Autowired
    private DeviceInfoQuery deviceInfoQuery;

    @Autowired
    private ExtensionExecutor extensionExecutor;

    @Autowired
    private IMetrics metrics;

    @Override
    public boolean fetchData(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        //取得应用类型，并调用到上下文中
        //从black_box的base64 解码后json串，取得appType.(注：forseti-api是根据传的app_name取得appType)
        Map<String, Object> param = new HashMap<>();
        String blackBox = request.getBlackBox();
        if (StringUtils.isBlank(blackBox)) {
            FpReasonUtils.put(param, FpReasonCodeEnum.NO_FP_PARAM_ERROR);
            context.setDeviceInfo(param);
            return true;
        }

        if (StringUtils.equalsIgnoreCase("FMAgent_instance_error", blackBox)) {
            FpReasonUtils.put(param, FpReasonCodeEnum.FMAGENT_INSTANCE_ERROR);
            context.setDeviceInfo(param);
            return true;
        }

        //调用设备指纹
        String respDetailType = request.getRespDetailType();
        String tokenId = request.getTokenId();
        Map<String, Object> deviceMap = invokeFingerPrint(context, context.getPartnerCode(), context.getAppName(), tokenId, blackBox, respDetailType);
//        logger.info("invokeFingerPrint result:{}", JSON.toJSONString(deviceMap));
        context.setDeviceInfo(deviceMap);

        String appType = deviceMap.get("appOs") == null ? null : deviceMap.get("appOs").toString();
        if (StringUtils.isBlank(context.getAppType()) && StringUtils.isNotBlank(appType)) {
            context.setAppType(appType);
        }

        boolean success = DataUtil.toBoolean(deviceMap.get("success"));
        if (!success) {
            deviceMap.put("success", false);
            //postProcessAfterFail(blackBox, deviceMap, jsonMap, version);
        } else {
            postProcessAfterSuccess(context, deviceMap, appType);
        }

        return true;
    }

    private void postProcessAfterSuccess(AbstractFraudContext context, Map<String, Object> deviceMap, String appType) {

        Object smartIdObj = deviceMap.get("smartId");
        if (smartIdObj != null) {
            context.set("smartId", smartIdObj.toString());
        }
        Object deviceIdObj = deviceMap.get("deviceId");
        if (deviceIdObj != null) {
            context.set("deviceId", deviceIdObj.toString());
        }
        //处理真实IP
        dealWithTrueIp(context, deviceMap);

        postProcessMobileScene(appType, context, deviceMap);
    }

    private void postProcessMobileScene(String appType, AbstractFraudContext context, Map jsonMap) {

        if ("Android".equalsIgnoreCase(appType)) {
            String phoneNumberStr = jsonMap.get("phoneNumber") == null ? null : jsonMap.get("phoneNumber").toString();
            if (phoneNumberStr != null) {
                Integer length = phoneNumberStr.length();
                if (length > 11) {
                    phoneNumberStr = phoneNumberStr.substring(length - 11, length);
                }
                context.set("deviceMobile", phoneNumberStr);
            }

            String bssidStr = jsonMap.get("bssid") == null ? null : jsonMap.get("bssid").toString();
            if (bssidStr != null) {
                context.set("deviceBssid", bssidStr);
            }

        }
        if ("iOS".equalsIgnoreCase(appType)) {
            String bssidStr = jsonMap.get("bssid") == null ? null : jsonMap.get("bssid").toString();
            if (bssidStr != null) {
                context.set("deviceBssid", bssidStr);
            }
        }
    }

    private void dealWithTrueIp(AbstractFraudContext context, Map<String, Object> deviceMap) {
        // 处理geoIp
        String trueIp = deviceMap.get("trueIp") + "";
        if (StringUtils.isNotEmpty(trueIp)) {
            GeoipEntity geoip = null;
            try {
                geoip = extensionExecutor.execute(GeoIpServiceExtPt.class, context.getBizScenario(), extension -> extension.getIpInfo(trueIp,context));
                context.set("trueIpAddressCountry",geoip.getCountry());
                context.set("trueIpAddressProvince",geoip.getProvince());
                context.set("trueIpAddressCity",geoip.getCity());
                context.set("trueIpAddressCountryCode",geoip.getCountryId());
                logger.info("真实geoip的数据结果:"+ JSON.toJSONString(geoip));
            } catch (Exception e) {
                logger.warn(TraceUtils.getFormatTrace() + "GeoIp查询 IP查询错误", e);
            }
            if (null != geoip) {
                deviceMap.put("geoIp", geoip);
            }
        }
    }

    private Map<String, Object> invokeFingerPrint(AbstractFraudContext context, String partnerCode, String appName, String tokenId, String blackBox, String respDetailType) {

        Map<String, Object> result = new HashMap<>();
        String paramDetailType = getDetailType(respDetailType);
        QueryParams params = new QueryParams();
        params.setAppName(appName);
        params.setBlackBox(blackBox);
        params.setPartnerCode(partnerCode);
        params.setTokenId(tokenId);
        params.setSequenceId(context.getSeqId());

        if (StringUtils.isNoneBlank()) {
            params.setResponseType(paramDetailType);
        }
        BaseResult<DeviceResp> baseResult = null;
        logger.info("deviceInfoQuery.query params :{}", JSON.toJSONString(params));
        try {
            String[] tags = {
                    MetricsConstant.METRICS_TAG_API_QPS_KEY,"fp.dubbo.DeviceInfoQuery"};
            metrics.counter(MetricsConstant.METRICS_API_QPS_KEY,tags);
            ITimeContext timeContext = metrics.metricTimer(MetricsConstant.METRICS_API_RT_KEY,tags);
            baseResult = deviceInfoQuery.query(params);
            timeContext.stop();
            if (null == baseResult) {
                logger.warn(TraceUtils.getFormatTrace() + "deviceInfoQuery.query result is null,blackBox:" + blackBox);
                FpReasonUtils.put(result, FpReasonCodeEnum.NO_RESULT_ERROR);
                return result;
            }
            if (baseResult.isSuccess()) {
                result.putAll(baseResult.getResult().getDeviceMap());
                result.put("success", true);
            } else {
                FpReasonUtils.put(result, baseResult.getCode(), baseResult.getMsg());
            }
            return result;

        } catch (Exception e) {
            if (ReasonCodeUtil.isTimeout(e)) {
                ReasonCodeUtil.add(context, ReasonCode.FP_QUERY_TIMEOUT, "fp");
                logger.warn(TraceUtils.getFormatTrace() + "deviceInfoQuery query timeout,blackBox:" + blackBox, e);
                FpReasonUtils.put(result, FpReasonCodeEnum.INVOKE_TIMEOUT_ERROR);
                if (null != result.get("code") && null != result.get("message")) {
                    ReasonCodeUtil.addExtCode(context, ReasonCode.FP_QUERY_TIMEOUT.getCode(), ReasonCode.FP_QUERY_TIMEOUT.getDescription(), "FP", "DeviceInfoQuery", result.get("code").toString(), result.get("message").toString());
                }
            } else {
                ReasonCodeUtil.add(context, ReasonCode.FP_QUERY_ERROR, "fp");
                logger.warn(TraceUtils.getFormatTrace() + "deviceInfoQuery query error,blackBox:" + blackBox, e);
                FpReasonUtils.put(result, FpReasonCodeEnum.CONNECT_ERROR);
            }
            return result;
        }
    }

    private String getDetailType(String respDetailType) {
        if (StringUtils.isBlank(respDetailType)) {
            return null;
        }
        String[] splits = respDetailType.toUpperCase().split(",");
        Set<String> stringSet = Sets.newHashSet(splits);
        if (stringSet.contains(RespDetailTypeEnum.DEVICE.name())) {
            return RespDetailTypeEnum.DEVICE.name();
        } else if (stringSet.contains(RespDetailTypeEnum.DEVICE_ALL.name())) {
            return RespDetailTypeEnum.DEVICE_ALL.name();
        } else {
            return respDetailType;
        }
    }
}
