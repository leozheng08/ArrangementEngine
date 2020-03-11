package cn.tongdun.kunpeng.api.application.ruledata;

import cn.tongdun.gaea.client.common.IndicatrixRetCode;
import cn.tongdun.gaea.client.common.base.Result;
//import cn.tongdun.gaea.paas.api.GaeaPaasApi;
//import cn.tongdun.gaea.paas.dto.PaasGaeaIndicatrixVal;
//import cn.tongdun.gaea.paas.dto.PassIndicatrixValQuery;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.PlatformIndexCache;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinitionCache;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.PlatformIndexData;
import cn.tongdun.kunpeng.common.data.ReasonCode;
import cn.tongdun.kunpeng.common.util.ReasonCodeUtil;
import cn.tongdun.tdframework.core.pipeline.Step;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Step(pipeline = Risk.NAME,phase = Risk.RULE_DATA,order = 1100)
public class PlatformIndexStep implements IRiskStep {

//    @Autowired
//    private GaeaPaasApi gaeaPaasApi;

    @Autowired
    private PlatformIndexCache policyIndicatrixItemCache;

    @Autowired
    private FieldDefinitionCache fieldDefinitionCache;

    private static final String APP_NAME = "default";


    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
//        // 1.取实时解析的gaea缓存
//        List<String> indicatrixs = policyIndicatrixItemCache.getList(context.getPolicyUuid());
//
//        Map<String, Object> activityParam = getGaeaFields(context);
//
//        List<Long> indicatrixsParam = new ArrayList<>();
//        for (String key : indicatrixs) {
//            if (StringUtils.isBlank(key)) {
//                continue;
//            }
//            try {
//                indicatrixsParam.add(Long.valueOf(key));
//            } catch (Exception e) {
//                continue;
//            }
//        }
//
//        try {
//            PassIndicatrixValQuery passIndicatrixValQuery = new PassIndicatrixValQuery();
//            passIndicatrixValQuery.setBizId(context.getSeqId());
//            passIndicatrixValQuery.setPartnerCode(context.getPartnerCode());
//            passIndicatrixValQuery.setEventType(context.getEventType());
//            passIndicatrixValQuery.setEventId(context.getEventId());
//            passIndicatrixValQuery.setAppName(APP_NAME);
//            passIndicatrixValQuery.setActivity(activityParam);
//            passIndicatrixValQuery.setIndicatrixIds(indicatrixsParam);
//
//            Result<List<PaasGaeaIndicatrixVal>> indicatrixResult = null;
//
//            try {
//                // 根据指标ID计算,适用于延迟敏感型场景(p999 50ms)
//                indicatrixResult = gaeaPaasApi.calcMulti(passIndicatrixValQuery);
//            } catch (Exception e) {
//                // 临时通过LocalcachePeriod配置项做下开关
//                if (ReasonCodeUtil.isTimeout(e)) {
//                    ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_TIMEOUT, "gaea");
//                }
//                logger.error("Error occurred when {} indicatrix result for {}.", context.getSeqId(), JSON.toJSONString(indicatrixsParam), e);
//            }
//
//            if (indicatrixResult != null && indicatrixResult.getVal() != null) {
//                for (PaasGaeaIndicatrixVal item : indicatrixResult.getVal()) {
//                    resolveGaeaValue(context, item);
//                }
//            }
//
//        } catch (Exception e) {
//            logger.error("Error occurred when GaeaIndexCalculateImpl fetchData.", e);
//        }

        return true;
    }


//    public void resolveGaeaValue(AbstractFraudContext context, PaasGaeaIndicatrixVal indicatrixVal) {
//
//        int retCode = indicatrixVal.getRetCode();
//        if (indicatrixVal.getRetCode() < 500) {
//            if (indicatrixVal.getIndicatrixId() == null) {
//                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, "gaea");
//                logger.warn("指标读取异常,gaea返回结果：{}，中indicatrixId值为空", indicatrixVal.toString());
//                return;
//            }
//
//            String indicatrixId = indicatrixVal.getIndicatrixId().toString();
//            PlatformIndexData indexData = setPlatformIndexData(indicatrixVal, parseDouble(context, indicatrixVal.getResult()));
//            context.putPlatformIndexMap(indicatrixId, indexData);
//            if (retCode == IndicatrixRetCode.INDEX_ERROR.getCode()) {
//                logger.error("合作方没有此指标,合作方：{}， 指标：{}", context.getPartnerCode(), indicatrixVal.getIndicatrixId());
//            }
//        } else {
//            if (retCode == 600) {
//                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_LIMITING, "gaea");
//                logger.warn("gaea指标:{}获取限流", indicatrixVal.getIndicatrixId());
//            } else if (retCode == 508) {
//                ReasonCodeUtil.add(context, ReasonCode.GAEA_FLOW_ERROR, "gaea");
//                logger.warn("gaea指标:{}指标流量不足", indicatrixVal.getIndicatrixId());
//            } else {
//                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, "gaea");
//                logger.warn("gaea指标:{}指标读取异常", indicatrixVal.getIndicatrixId());
//            }
//        }
//    }
//
//
//    public Map<String, Object> getGaeaFields(AbstractFraudContext context) {
//        Map<String, Object> gaeaContext = new HashMap<>();
//
//        //系统字段
//        List<FieldDefinition> systemFields = fieldDefinitionCache.getSystemField(context);
//
//        //扩展字段
//        List<FieldDefinition> extendFields = fieldDefinitionCache.getExtendField(context);
//
//        if (CollectionUtils.isNotEmpty(systemFields)) {
//            systemFields.forEach(fieldDefinition -> {
//                String fieldName = fieldDefinition.getFieldName();
//                Object v = context.get(fieldName);
//                if (null != v) {
//                    gaeaContext.put(fieldName, v);
//                }
//            });
//        }
//
//        if (CollectionUtils.isNotEmpty(extendFields)) {
//            extendFields.forEach(fieldDefinition -> {
//                String fieldName = fieldDefinition.getFieldName();
//                Object v = context.get(fieldName);
//                if (null != v) {
//                    gaeaContext.put(fieldName, v);
//                }
//            });
//        }
//        return gaeaContext;
//    }
//
//    private Double parseDouble(AbstractFraudContext context, Object obj) {
//        if (obj == null) {
//            return null;
//        }
//        if (obj instanceof Double) {
//            return (Double) obj;
//        } else {
//            try {
//                return Double.parseDouble(obj.toString());
//            } catch (Exception e) {
//                logger.error("gata return result :" + obj + " can't parse to Double", e);
//                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, "gaea");
//                return null;
//            }
//
//        }
//    }
//
//    public PlatformIndexData setPlatformIndexData(PaasGaeaIndicatrixVal indicatrixVal, Double result) {
//        if (result == null) {
//            result = Double.NaN;
//        }
//        PlatformIndexData indexData = new PlatformIndexData();
//        indexData.setValue(result);
//        indexData.setOriginalValue(indicatrixVal.getResult());
//        indexData.setDetail(indicatrixVal.getPaasConditionDetail());
//        return indexData;
//    }

}
