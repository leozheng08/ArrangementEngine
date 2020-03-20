package cn.tongdun.kunpeng.api.application.ruledata;

import cn.tongdun.gaea.client.common.IndicatrixRetCode;
import cn.tongdun.gaea.paas.api.GaeaApi;
import cn.tongdun.gaea.paas.dto.GaeaIndicatrixVal;
import cn.tongdun.gaea.paas.dto.IndicatrixValQuery;
import cn.tongdun.gaea.paas.dto.PaasResult;
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

    @Autowired
    private GaeaApi gaeaApi;

    @Autowired
    private PlatformIndexCache policyIndicatrixItemCache;

    @Autowired
    private FieldDefinitionCache fieldDefinitionCache;

    private static final String APP_NAME = "default";


    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        // 1.取实时解析的gaea缓存
        List<String> indicatrixs = policyIndicatrixItemCache.getList(context.getPolicyUuid());

        if(indicatrixs == null || indicatrixs.isEmpty()){
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
                continue;
            }
        }

        if(indicatrixsParam.isEmpty()){
            return true;
        }

        try {
//            IndicatrixValQuery indicatrixValQuery = new IndicatrixValQuery();
//            indicatrixValQuery.setBizId(context.getSeqId());
//            indicatrixValQuery.setPartnerCode(context.getPartnerCode());
//            indicatrixValQuery.setEventType(context.getEventType());
//            indicatrixValQuery.setEventId(context.getEventId());
//            indicatrixValQuery.setAppName(APP_NAME);
//            indicatrixValQuery.setActivity(activityParam);
//            indicatrixValQuery.setIndicatrixIds(indicatrixsParam);

            /****************测试代码*********************/
            //测试代码
            indicatrixsParam = new ArrayList<>();
            String idStr = "110765508504228841,110769923604820969,110770896230364137,110781052020081641,111057807721268201,111062910037558249,111063317698741225,111063641419318249,111156222018974697,111156970001560553";
            for (String id : idStr.split(",")) {
                indicatrixsParam.add(Long.parseLong(id));
            }
            List<String> codeList = new ArrayList<>();
            String codeStr = "c3b2fba3b64d5671,290a6efdee0b4724,daf4496d889628de,33744122d5934261,8b249be93f48da0d,03b3d4b9a94fa2bd,cfb3860347879efa,8b75e4c9cee31c9b,4eb75aa639c945a4,e3b68a0d447391ff";
            for (String code : codeStr.split(",")) {
                codeList.add(code);
            }
            IndicatrixValQuery indicatrixValQuery = new IndicatrixValQuery();
            indicatrixValQuery.setBizId(context.getSeqId());
            indicatrixValQuery.setPartnerCode("demo");
            indicatrixValQuery.setEventType(context.getEventType());
            indicatrixValQuery.setEventId(context.getEventId());
            indicatrixValQuery.setAppName(APP_NAME);
            indicatrixValQuery.setActivity(activityParam);
            indicatrixValQuery.setIndicatrixIds(indicatrixsParam);
            indicatrixValQuery.setMeaningCodes(codeList);
            /*************************************/

            PaasResult<List<GaeaIndicatrixVal>> indicatrixResult = null;
            try {
                // 根据指标ID计算,适用于延迟敏感型场景(p999 50ms)
                indicatrixResult = gaeaApi.calcMulti(indicatrixValQuery);
                logger.info("平台指标响应结果：{}", JSON.toJSONString(indicatrixResult));
            } catch (Exception e) {
                // 临时通过LocalcachePeriod配置项做下开关
                if (ReasonCodeUtil.isTimeout(e)) {
                    ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_TIMEOUT, "gaea");
                }
                logger.error("Error occurred when {} indicatrix result for {}.", context.getSeqId(), JSON.toJSONString(indicatrixsParam), e);
            }
            if (null != indicatrixResult && indicatrixResult.isSuccess()) {
                for (GaeaIndicatrixVal indicatrixVal : indicatrixResult.getData()) {
                    resolveGaeaValue(context, indicatrixVal);
                }
            }
        } catch (Exception e) {
            logger.error("Error occurred when GaeaIndexCalculateImpl fetchData.", e);
        }

        return true;
    }


    public void resolveGaeaValue(AbstractFraudContext context, GaeaIndicatrixVal indicatrixVal) {

        int retCode = indicatrixVal.getRetCode();
        if (indicatrixVal.getRetCode() < 500) {
            if (indicatrixVal.getIndicatrixId() == null) {
                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, "gaea");
                logger.warn("指标读取异常,gaea返回结果：{}，中indicatrixId值为空", indicatrixVal.toString());
                return;
            }

            String indicatrixId = indicatrixVal.getIndicatrixId().toString();
            PlatformIndexData indexData = setPlatformIndexData(indicatrixVal, parseDouble(context, indicatrixVal.getResult()));
            context.putPlatformIndexMap(indicatrixId, indexData);
            if (retCode == IndicatrixRetCode.INDEX_ERROR.getCode()) {
                logger.error("合作方没有此指标,合作方：{}， 指标：{}", context.getPartnerCode(), indicatrixVal.getIndicatrixId());
            }
        } else {
            if (retCode == 600) {
                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_LIMITING, "gaea");
                logger.warn("gaea指标:{}获取限流", indicatrixVal.getIndicatrixId());
            } else if (retCode == 508) {
                ReasonCodeUtil.add(context, ReasonCode.GAEA_FLOW_ERROR, "gaea");
                logger.warn("gaea指标:{}指标流量不足", indicatrixVal.getIndicatrixId());
            } else {
                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, "gaea");
                logger.warn("gaea指标:{}指标读取异常", indicatrixVal.getIndicatrixId());
            }
        }
    }


    public Map<String, Object> getGaeaFields(AbstractFraudContext context) {
        Map<String, Object> gaeaContext = new HashMap<>();
        //系统字段
        Collection<FieldDefinition> systemFields = fieldDefinitionCache.getSystemField(context);
        //扩展字段
        Collection<FieldDefinition> extendFields = fieldDefinitionCache.getExtendField(context);

        build(context, systemFields, gaeaContext);
        build(context, extendFields, gaeaContext);
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
                logger.error("gata return result :" + obj + " can't parse to Double", e);
                ReasonCodeUtil.add(context, ReasonCode.INDICATRIX_QUERY_ERROR, "gaea");
                return null;
            }

        }
    }

    public PlatformIndexData setPlatformIndexData(GaeaIndicatrixVal indicatrixVal, Double result) {
        if (result == null) {
            result = Double.NaN;
        }
        PlatformIndexData indexData = new PlatformIndexData();
        indexData.setValue(result);
        indexData.setOriginalValue(indicatrixVal.getResult());
        indexData.setDetail(indicatrixVal.getConditionDetail());
        return indexData;
    }


    private void build(AbstractFraudContext context, Collection<FieldDefinition> fields, Map<String, Object> gaeaContext) {
        if (CollectionUtils.isNotEmpty(fields)) {
            fields.forEach(fieldDefinition -> {
                String fieldCode = fieldDefinition.getFieldCode();
                Object v = context.get(fieldCode);
                if (null != v) {
                    gaeaContext.put(fieldCode, v);
                }
            });
        }
    }
}
