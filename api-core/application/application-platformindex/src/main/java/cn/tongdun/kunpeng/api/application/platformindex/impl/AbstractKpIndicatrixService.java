package cn.tongdun.kunpeng.api.application.platformindex.impl;

import cn.tongdun.gaea.client.common.IndicatrixRetCode;
import cn.tongdun.kunpeng.api.application.platformindex.KpIndicatrixService;
import cn.tongdun.kunpeng.api.application.platformindex.pojo.IndicatrixApiResult;
import cn.tongdun.kunpeng.api.application.platformindex.pojo.IndicatrixRequest;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.IFieldDefinition;
import cn.tongdun.kunpeng.api.common.data.PlatformIndexData;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.metrics.IMetrics;
import cn.tongdun.tdframework.core.metrics.ITimeContext;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jie
 * @date 2020/12/15
 */
@Component
public abstract class AbstractKpIndicatrixService<R> implements KpIndicatrixService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractKpIndicatrixService.class);

    protected static final String METRICS_TAG_API_QPS_KEY = "dubbo_qps";
    protected static final String METRICS_TAG_PARTNER_KEY = "partner_code";
    protected static final String METRICS_API_QPS_KEY = "kunpeng.api.dubbo.qps";
    protected static final String METRICS_API_RT_KEY = "kunpeng.api.dubbo.rt";
    protected static final String METRICS_API_PARTNER_RT_KEY = "kunpeng.api.dubbo.partner.rt";

    @Autowired
    private IMetrics metrics;

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
            metrics.counter(METRICS_API_QPS_KEY,tags);
            ITimeContext timeContext = metrics.metricTimer(METRICS_API_RT_KEY,tags);

            String[] partnerTags = {
                    METRICS_TAG_PARTNER_KEY, indicatrixRequest.getPartnerCode()};
            ITimeContext timePartner = metrics.metricTimer(METRICS_API_PARTNER_RT_KEY,partnerTags);

            // 具体指标接口实现
            apiResult = this.calculateByIds(indicatrixRequest);

            timeContext.stop();
            timePartner.stop();
        } catch (Exception e) {
            // 临时通过LocalcachePeriod配置项做下开关
            if (ReasonCodeUtil.isTimeout(e)) {
                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_TIMEOUT, getApiTag());
            } else {
                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, getApiTag());
            }
            logger.error(TraceUtils.getFormatTrace()+"Error occurred when {} indicatrix result for {}.", indicatrixRequest.getBizId(), JSON.toJSONString(indicatrixRequest), e);
        }

        // 3. 重试
        timeOutRetry(context, indicatrixRequest,apiResult);

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
     * @return
     */
    protected abstract String getApiTag();

    /**
     * 重试逻辑
     * @param context
     * @param indicatrixRequest
     * @param apiResult
     */
    protected void timeOutRetry(AbstractFraudContext context, IndicatrixRequest indicatrixRequest, IndicatrixApiResult<List<PlatformIndexData>> apiResult) {
        //  印尼版本先不做
    }

    /**
     * 解析单个指标结果
     * @param context
     * @param indicatrixVal
     */
    protected void resolveGaeaValue(AbstractFraudContext context, PlatformIndexData indicatrixVal){
        if (null==indicatrixVal){
            ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, "gaea");
            logger.warn(TraceUtils.getFormatTrace()+"指标读取异常,indicatrixVal值为null!");
            return;
        }

        int retCode = indicatrixVal.getRetCode();
        if (retCode < 500) {
            if (indicatrixVal.getIndicatrixId() == null) {
                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, "gaea");
                logger.warn(TraceUtils.getFormatTrace()+"指标读取异常,gaea返回结果：{}，中indicatrixId值为空", indicatrixVal.toString());
                return;
            }

            String indicatrixId = indicatrixVal.getIndicatrixId().toString();
            context.putPlatformIndexMap(indicatrixId, indicatrixVal);
            if (retCode == IndicatrixRetCode.INDEX_ERROR.getCode()) {
                logger.error(TraceUtils.getFormatTrace()+"合作方没有此指标,合作方：{}， 指标：{}", context.getPartnerCode(), indicatrixId);
            }
        } else {
            if (retCode == 600) {
                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_LIMITING, "gaea");
                logger.warn(TraceUtils.getFormatTrace()+"gaea指标:{}获取限流", indicatrixVal.getIndicatrixId());
            } else if (retCode == 508) {
                ReasonCodeUtil.add(context, ReasonCode.GAEA_FLOW_ERROR, "gaea");
                logger.warn(TraceUtils.getFormatTrace()+"gaea指标:{}指标流量不足", indicatrixVal.getIndicatrixId());
            } else {
                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, "gaea");
                logger.warn(TraceUtils.getFormatTrace()+"gaea指标:{}指标读取异常", indicatrixVal.getIndicatrixId());
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
     * @param context
     * @return
     */
    protected Map<String, Object> getGaeaFields(AbstractFraudContext context) {
        Map<String, Object> gaeaContext = new HashMap<>();
        //系统字段
        Map<String, IFieldDefinition> systemFieldMap=context.getSystemFieldMap();
        //扩展字段
        Map<String,IFieldDefinition> extendFieldMap=context.getExtendFieldMap();

        build(context, systemFieldMap, gaeaContext);
        build(context, extendFieldMap, gaeaContext);
        return gaeaContext;
    }

    protected void build(AbstractFraudContext context, Map<String,IFieldDefinition> systemFieldMap, Map<String, Object> gaeaContext) {
        if (null!=systemFieldMap&&!systemFieldMap.isEmpty()) {
            systemFieldMap.forEach((k,v)-> {
                Object fieldValue = context.get(k);
                if (null != fieldValue) {
                    gaeaContext.put(k, fieldValue);
                }
            });
        }
    }

    /**
     * 组装指标平台参数
     * @param context
     * @return
     */
    protected abstract IndicatrixRequest generateIndicatrixRequest(AbstractFraudContext context);

    /**
     * 指标平台接口返回转换成统一结果对象
     * @param apiResult
     * @return
     */
    protected abstract IndicatrixApiResult<List<PlatformIndexData>> convertApiResult(R apiResult);
}
