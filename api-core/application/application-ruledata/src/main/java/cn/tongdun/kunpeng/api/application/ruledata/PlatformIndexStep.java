package cn.tongdun.kunpeng.api.application.ruledata;

import cn.tongdun.gaea.client.common.IndicatrixRetCode;
import cn.tongdun.gaea.paas.api.GaeaApi;
import cn.tongdun.gaea.paas.dto.GaeaIndicatrixVal;
import cn.tongdun.gaea.paas.dto.IndicatrixValQuery;
import cn.tongdun.gaea.paas.dto.PaasResult;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.IFieldDefinition;
import cn.tongdun.kunpeng.api.common.data.PlatformIndexData;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.PlatformIndexCache;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinitionCache;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.metrics.IMetrics;
import cn.tongdun.tdframework.core.metrics.ITimeContext;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Step(pipeline = Risk.NAME,phase = Risk.RULE_DATA,order = 1100)
public class PlatformIndexStep implements IRiskStep {

    @Autowired
    private GaeaApi gaeaApi;

    @Autowired
    private PlatformIndexCache policyIndicatrixItemCache;

    @Autowired
    private FieldDefinitionCache fieldDefinitionCache;

    @Autowired
    private IMetrics metrics;


    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        // 1.取实时解析的gaea缓存
        List<String> indicatrixs = policyIndicatrixItemCache.getList(context.getPolicyUuid());

        if(indicatrixs == null || indicatrixs.isEmpty()){
            logger.info(TraceUtils.getFormatTrace()+"策略id:{}，没有从gaea缓存取到指标信息", context.getPolicyUuid());
            return true;
        }

        Map<String, Object> activityParam = getGaeaFields(context);

        List<Long> indicatrixsParam = new ArrayList<>();
        for (String key : indicatrixs) {
            if (StringUtils.isBlank(key)) {
                continue;
            }
            try {
                indicatrixsParam.add(Long.valueOf(key));
            } catch (Exception e) {
                logger.error(TraceUtils.getFormatTrace() + "PlatformIndexStep param parse error,key:" + key, e);
                continue;
            }
        }

        if(indicatrixsParam.isEmpty()){
            logger.info(TraceUtils.getFormatTrace()+"策略id:{}，从缓存中取指标数组为空", context.getPolicyUuid());
            return true;
        }

        try {
            IndicatrixValQuery indicatrixValQuery = new IndicatrixValQuery();
            indicatrixValQuery.setBizId(context.getSeqId());
            indicatrixValQuery.setPartnerCode(context.getPartnerCode());
            indicatrixValQuery.setEventType(context.getEventType());
            indicatrixValQuery.setEventId(context.getEventId());
            indicatrixValQuery.setAppName(context.getAppName());
            indicatrixValQuery.setActivity(activityParam);
            indicatrixValQuery.setEventOccurTime(context.getEventOccurTime().getTime());
            indicatrixValQuery.setIndicatrixIds(indicatrixsParam);
            indicatrixValQuery.setNeedDetail(true);

            PaasResult<List<GaeaIndicatrixVal>> indicatrixResult = null;
            try {
                // 根据指标ID计算,适用于延迟敏感型场景(p999 50ms)
                String[] tags = {
                        "dubbo_qps","paas.api.GaeaApi"};
                metrics.counter("kunpeng.api.dubbo.qps",tags);
                ITimeContext timeContext = metrics.metricTimer("kunpeng.api.dubbo.rt",tags);

                String[] partnerTags = {
                        "partner_code",request.getPartnerCode()};
                ITimeContext timePartner = metrics.metricTimer("kunpeng.api.dubbo.partner.rt",partnerTags);

                indicatrixResult = gaeaApi.calcMulti(indicatrixValQuery);
                timeContext.stop();
                timePartner.stop();
                // TODO 上线前移除
                request.getFieldValues().put("indicatrix_result", JSON.toJSONString(indicatrixResult.getData()));
                logger.info(TraceUtils.getFormatTrace()+"平台指标响应结果：{}", JSON.toJSONString(indicatrixResult));
            } catch (Exception e) {
                // 临时通过LocalcachePeriod配置项做下开关
                if (ReasonCodeUtil.isTimeout(e)) {
                    ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_TIMEOUT, "gaea");
                } else {
                    ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, "gaea");
                }
                logger.error(TraceUtils.getFormatTrace()+"Error occurred when {} indicatrix result for {}.", context.getSeqId(), JSON.toJSONString(indicatrixsParam), e);
            }
            if (null != indicatrixResult && indicatrixResult.isSuccess()) {
                for (GaeaIndicatrixVal indicatrixVal : indicatrixResult.getData()) {
                    resolveGaeaValue(context, indicatrixVal);
                }
            }
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace()+"Error occurred when GaeaIndexCalculateImpl fetchData.", e);
        }

        return true;
    }


    public void resolveGaeaValue(AbstractFraudContext context, GaeaIndicatrixVal indicatrixVal) {

        int retCode = indicatrixVal.getRetCode();
        if (indicatrixVal.getRetCode() < 500) {
            if (indicatrixVal.getIndicatrixId() == null) {
                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, "gaea");
                logger.warn(TraceUtils.getFormatTrace()+"指标读取异常,gaea返回结果：{}，中indicatrixId值为空", indicatrixVal.toString());
                return;
            }

            String indicatrixId = indicatrixVal.getIndicatrixId().toString();
            PlatformIndexData indexData = setPlatformIndexData(indicatrixVal, parseDouble(context, indicatrixVal.getResult()));
            context.putPlatformIndexMap(indicatrixId, indexData);
            if (retCode == IndicatrixRetCode.INDEX_ERROR.getCode()) {
                logger.error(TraceUtils.getFormatTrace()+"合作方没有此指标,合作方：{}， 指标：{}", context.getPartnerCode(), indicatrixVal.getIndicatrixId());
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


    public Map<String, Object> getGaeaFields(AbstractFraudContext context) {
        Map<String, Object> gaeaContext = new HashMap<>();
        //系统字段
        Map<String,IFieldDefinition> systemFieldMap=context.getSystemFieldMap();
        //扩展字段
        Map<String,IFieldDefinition> extendFieldMap=context.getExtendFieldMap();

        build(context, systemFieldMap, gaeaContext);
        build(context, extendFieldMap, gaeaContext);
        return gaeaContext;
    }

    private Double parseDouble(AbstractFraudContext context, Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Double) {
            return (Double) obj;
        } else {
            try {
                return Double.parseDouble(obj.toString());
            } catch (Exception e) {
                logger.error(TraceUtils.getFormatTrace()+"gata return result :" + obj + " can't parse to Double", e);
                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, "gaea");
                return null;
            }

        }
    }

    public PlatformIndexData setPlatformIndexData(GaeaIndicatrixVal indicatrixVal, Double result) {

        PlatformIndexData indexData = new PlatformIndexData();
        indexData.setValue(result == null ? 0D : result);
        indexData.setOriginalValue(indicatrixVal.getResult());
        indexData.setStringValue(indicatrixVal.getStrResult());
        indexData.setDetail(indicatrixVal.getConditionDetail());
        return indexData;
    }


    private void build(AbstractFraudContext context, Map<String,IFieldDefinition> systemFieldMap, Map<String, Object> gaeaContext) {
        if (null!=systemFieldMap&&!systemFieldMap.isEmpty()) {
            systemFieldMap.forEach((k,v)-> {
                Object fieldValue = context.get(k);
                if (null != fieldValue) {
                    gaeaContext.put(k, fieldValue);
                }
            });
        }
    }
}
