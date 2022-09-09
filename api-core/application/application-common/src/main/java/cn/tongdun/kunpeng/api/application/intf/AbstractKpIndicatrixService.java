package cn.tongdun.kunpeng.api.application.intf;

import cn.tongdun.gaea.client.common.IndicatrixRetCode;
import cn.tongdun.kunpeng.api.application.pojo.IndicatrixApiResult;
import cn.tongdun.kunpeng.api.application.pojo.IndicatrixRequest;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.IFieldDefinition;
import cn.tongdun.kunpeng.api.common.data.PlatformIndexData;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.PlatformIndexCache;
import cn.tongdun.kunpeng.api.engine.model.dictionary.DictionaryManager;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.metrics.IMetrics;
import cn.tongdun.tdframework.core.metrics.ITimeContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static cn.tongdun.kunpeng.api.common.MetricsConstant.*;

/**
 * @author jie
 * @date 2020/12/15
 */
@Component
public abstract class AbstractKpIndicatrixService<R> implements KpIndicatrixService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractKpIndicatrixService.class);

    @Autowired
    private IMetrics metrics;

    @Autowired
    private PlatformIndexCache policyIndicatrixItemCache;

    @Autowired
    private DictionaryManager dictionaryManager;

    @Override
    public IndicatrixApiResult<List<PlatformIndexData>> calculateByIdsAndSetContext(AbstractFraudContext context) {
        // 1. 组装参数
        IndicatrixRequest indicatrixRequest = generateIndicatrixRequest(context);
        if (indicatrixRequest == null || CollectionUtils.isEmpty(indicatrixRequest.getIndicatrixIds())) {
            return null;
        }

        // 2. 发起调用
        IndicatrixApiResult<List<PlatformIndexData>> apiResult = null;
        try {
            String[] tags = {
                    METRICS_TAG_API_QPS_KEY, getApiTag()};
            metrics.counter(METRICS_API_QPS_KEY, tags);
            ITimeContext timeContext = metrics.metricTimer(METRICS_API_RT_KEY, tags);

            String[] partnerTags = {
                    METRICS_TAG_PARTNER_CODE, indicatrixRequest.getPartnerCode()};
            ITimeContext timePartner = metrics.metricTimer(METRICS_API_PARTNER_RT_KEY, partnerTags);

            // 具体指标接口实现
            apiResult = this.calculateByIds(indicatrixRequest);

            timeContext.stop();
            timePartner.stop();
        } catch (Exception e) {
            // 临时通过LocalcachePeriod配置项做下开关
            if (ReasonCodeUtil.isTimeout(e)) {
                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_TIMEOUT, getApiTag());
                logger.warn(TraceUtils.getFormatTrace() + "调用指标服务超时", e);
            } else {
                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, getApiTag());
                logger.error(TraceUtils.getFormatTrace() + "调用指标服务异常", e);
            }
        }

        // 3. 重试
        timeOutRetry(context, indicatrixRequest, apiResult);

        // 4. 解析指标结果,并设置到上下文
        if (null != apiResult && apiResult.isSuccess()) {
            for (PlatformIndexData indicatrixVal : apiResult.getData()) {
                try {
                    resolveGaeaValue(context, indicatrixVal);
                } catch (Exception e) {
                    ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, getApiTag());
                    logger.error(TraceUtils.getFormatTrace() + "parse gaea value error!", e);
                }
            }
        }
        return apiResult;
    }

    /**
     * 接口对接打点监控key生成
     *
     * @return
     */
    protected abstract String getApiTag();

    /**
     * 重试逻辑
     *
     * @param context
     * @param indicatrixRequest
     * @param apiResult
     */
    protected void timeOutRetry(AbstractFraudContext context, IndicatrixRequest indicatrixRequest, IndicatrixApiResult<List<PlatformIndexData>> apiResult) {
        //  印尼版本先不做
    }

    /**
     * 解析单个指标结果
     *
     * @param context
     * @param indicatrixVal
     */
    protected void resolveGaeaValue(AbstractFraudContext context, PlatformIndexData indicatrixVal) {
        if (null == indicatrixVal) {
            ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, "gaea");
//            logger.warn(TraceUtils.getFormatTrace() + "指标读取异常,indicatrixVal值为null!");
            return;
        }

        int retCode = indicatrixVal.getRetCode();
        if (retCode < 500) {
            if (indicatrixVal.getIndicatrixId() == null) {
                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, "gaea");
//                logger.info(TraceUtils.getFormatTrace() + "指标读取异常,gaea返回结果：{}，中indicatrixId值为空", indicatrixVal.toString());
                return;
            }

            if (retCode == IndicatrixRetCode.PARAMS_ERROR.getCode()) {
//                logger.info(TraceUtils.getFormatTrace() + "指标获取异常,gaea返回结果：{}，参数错误", indicatrixVal.toString());
                return;
            }

            String indicatrixId = indicatrixVal.getIndicatrixId().toString();
            context.putPlatformIndexMap(indicatrixId, indicatrixVal);
            if (retCode == IndicatrixRetCode.INDEX_ERROR.getCode()) {
//                logger.info(TraceUtils.getFormatTrace() + "合作方没有此指标,合作方：{}， 指标：{}", context.getPartnerCode(), indicatrixId);
            }
        } else {
//            logger.error(TraceUtils.getFormatTrace() + "指标返回异常,gaea返回结果：{}", indicatrixVal.toString());
            String subReasonCode = dictionaryManager.getReasonCode("gaea", String.valueOf(retCode));
            // 针对字典表中未配置的状态子码，暂时不做处理
            if (StringUtils.isNotEmpty(subReasonCode)) {
                String subReasonCodeMessage = dictionaryManager.getMessage(subReasonCode);
                ReasonCodeUtil.addExtCode(context, subReasonCode, subReasonCodeMessage, "gaea", "calculateByIds", String.valueOf(retCode), indicatrixVal.getDesc());
            }
        }
    }

    protected Double parseDouble(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Double) {
            return (Double) obj;
        } else {
            return Double.parseDouble(obj.toString());
        }
    }

    /**
     * 获取指标参数
     *
     * @param context
     * @return
     */
    protected Map<String, Object> getGaeaFields(AbstractFraudContext context) {
        Map<String, Object> gaeaContext = new HashMap<>();
        //系统字段
        Map<String, IFieldDefinition> systemFieldMap = context.getSystemFieldMap();
        //扩展字段
        Map<String, IFieldDefinition> extendFieldMap = context.getExtendFieldMap();

        build(context, systemFieldMap, gaeaContext);
        build(context, extendFieldMap, gaeaContext);

        gaeaContext.remove("location");
        if (context.getGeoipEntity() != null) {
            String city = context.getGeoipEntity().getCity();
            if (StringUtils.isNotBlank(city)) {
                gaeaContext.put("location", city);
            }
        }

        return gaeaContext;
    }

    protected void build(AbstractFraudContext context, Map<String, IFieldDefinition> systemFieldMap, Map<String, Object> gaeaContext) {

        ConcurrentHashMap<String, String> encryptionFields = context.getEncryptionFields();

        if (null != systemFieldMap && !systemFieldMap.isEmpty()) {
            systemFieldMap.forEach((k, v) -> {
                Object fieldValue = null;
                if (encryptionFields.containsKey(k)) {
                    String fieldValues = encryptionFields.get(k);
                    if (fieldValues.indexOf("##") != -1) {
                        fieldValue = fieldValues.substring(fieldValues.indexOf("##") + 2);
                    } else {
                        fieldValue = fieldValues;
                    }
                } else {
                    fieldValue = context.get(k);
                }
                if (null != fieldValue) {
                    gaeaContext.put(k, fieldValue);
                }
            });
        }
    }

    /**
     * 组装指标平台参数
     *
     * @param context
     * @return
     */
    protected IndicatrixRequest generateIndicatrixRequest(AbstractFraudContext context) {
        // 1.取实时解析的gaea缓存
        List<String> indicatrixs = policyIndicatrixItemCache.getList(context.getPolicyUuid());

        if (indicatrixs == null || indicatrixs.isEmpty()) {
            logger.info(TraceUtils.getFormatTrace() + "策略id:{}，没有从gaea缓存取到指标信息", context.getPolicyUuid());
            return null;
        }

        Map<String, Object> activityParam = getGaeaFields(context);

        Set<Long> indicatrixsParam = Sets.newHashSet();
        for (String key : indicatrixs) {
            if (StringUtils.isNotBlank(key)) {
                try {
                    indicatrixsParam.add(Long.valueOf(key));
                } catch (Exception e) {
                    logger.error(TraceUtils.getFormatTrace() + "PlatformIndexStep param parse error,key:" + key, e);
                    continue;
                }
            }
        }

        if (indicatrixsParam.isEmpty()) {
            logger.info(TraceUtils.getFormatTrace() + "策略id:{}，从缓存中取指标数组为空", context.getPolicyUuid());
            return null;
        }

        //增加sequenceId，与forseti对齐
        activityParam.put("sequenceId", context.getSeqId());

        IndicatrixRequest indicatrixRequest = new IndicatrixRequest();
        indicatrixRequest.setBizId(context.getSeqId());
        indicatrixRequest.setBizName("kunpeng-sea-api");
        indicatrixRequest.setPartnerCode(context.getPartnerCode());
        indicatrixRequest.setEventType(context.getEventType());
        indicatrixRequest.setEventId(context.getEventId());
        indicatrixRequest.setAppName(context.getAppName());
        indicatrixRequest.setActivity(activityParam);
        indicatrixRequest.setEventOccurTime(context.getEventOccurTime().getTime());
        indicatrixRequest.setIndicatrixIds(Lists.newArrayList(indicatrixsParam));
        indicatrixRequest.setNeedDetail(true);

        return indicatrixRequest;
    }

    /**
     * 指标平台接口返回转换成统一结果对象
     *
     * @param apiResult
     * @return
     */
    protected abstract IndicatrixApiResult<List<PlatformIndexData>> convertApiResult(R apiResult);
}
