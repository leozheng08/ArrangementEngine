package cn.tongdun.kunpeng.api.application.platformindex.impl;

import cn.tongdun.gaea.paas.api.GaeaApi;
import cn.tongdun.gaea.paas.dto.GaeaIndicatrixVal;
import cn.tongdun.gaea.paas.dto.IndicatrixValQuery;
import cn.tongdun.gaea.paas.dto.PaasResult;
import cn.tongdun.kunpeng.api.application.platformindex.pojo.IndicatrixApiResult;
import cn.tongdun.kunpeng.api.application.platformindex.pojo.IndicatrixRequest;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.PlatformIndexData;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.PlatformIndexCache;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 北美指标平台对接
 * @author jie
 * @date 2020/12/14
 */
public class GaeaPaasServiceImpl extends AbstractKpIndicatrixService<PaasResult<List<GaeaIndicatrixVal>>> {

    private static final Logger logger = LoggerFactory.getLogger(GaeaPaasServiceImpl.class);

    private GaeaApi gaeaApi;

    private PlatformIndexCache policyIndicatrixItemCache;

    public GaeaPaasServiceImpl(GaeaApi gaeaApi, PlatformIndexCache policyIndicatrixItemCache) {
        this.gaeaApi = gaeaApi;
        this.policyIndicatrixItemCache = policyIndicatrixItemCache;
    }

    @Override
    public IndicatrixApiResult<List<PlatformIndexData>> calculateByIds(IndicatrixRequest indicatrixRequest) throws Exception{
        PaasResult<List<GaeaIndicatrixVal>> indicatrixResult = null;
        IndicatrixValQuery indicatrixValQuery = new IndicatrixValQuery();
        indicatrixValQuery.setBizId(indicatrixRequest.getBizId());
        indicatrixValQuery.setPartnerCode(indicatrixRequest.getPartnerCode());
        indicatrixValQuery.setEventType(indicatrixRequest.getEventType());
        indicatrixValQuery.setEventId(indicatrixRequest.getEventId());
        indicatrixValQuery.setAppName(indicatrixRequest.getAppName());
        indicatrixValQuery.setActivity(indicatrixRequest.getActivity());
        indicatrixValQuery.setEventOccurTime(indicatrixRequest.getEventOccurTime());
        indicatrixValQuery.setIndicatrixIds(indicatrixRequest.getIndicatrixIds());
        indicatrixValQuery.setNeedDetail(true);

        // 根据指标ID计算,适用于延迟敏感型场景(p999 50ms)
        indicatrixResult = gaeaApi.calcMulti(indicatrixValQuery);

        return convertApiResult(indicatrixResult);
    }

    @Override
    protected IndicatrixRequest generateIndicatrixRequest(AbstractFraudContext context) {
        // 1.取实时解析的gaea缓存
        List<String> indicatrixs = policyIndicatrixItemCache.getList(context.getPolicyUuid());

        if(indicatrixs == null || indicatrixs.isEmpty()){
            logger.info(TraceUtils.getFormatTrace()+"策略id:{}，没有从gaea缓存取到指标信息", context.getPolicyUuid());
            return null;
        }

        Map<String, Object> activityParam = getGaeaFields(context);

        List<Long> indicatrixsParam = new ArrayList<>();
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

        if(indicatrixsParam.isEmpty()){
            logger.info(TraceUtils.getFormatTrace()+"策略id:{}，从缓存中取指标数组为空", context.getPolicyUuid());
            return null;
        }

        IndicatrixRequest indicatrixRequest = new IndicatrixRequest();
        indicatrixRequest.setBizId(context.getSeqId());
        indicatrixRequest.setPartnerCode(context.getPartnerCode());
        indicatrixRequest.setEventType(context.getEventType());
        indicatrixRequest.setEventId(context.getEventId());
        indicatrixRequest.setAppName(context.getAppName());
        indicatrixRequest.setActivity(activityParam);
        indicatrixRequest.setEventOccurTime(context.getEventOccurTime().getTime());
        indicatrixRequest.setIndicatrixIds(indicatrixsParam);
        indicatrixRequest.setNeedDetail(true);

        return indicatrixRequest;
    }

    @Override
    protected IndicatrixApiResult<List<PlatformIndexData>> convertApiResult(PaasResult<List<GaeaIndicatrixVal>> listPaasResult) {
        IndicatrixApiResult<List<PlatformIndexData>> result = null;
        if (null == listPaasResult) {
            return null;
        }

        result = new IndicatrixApiResult<>();
        result.setSuccess(listPaasResult.isSuccess());
        result.setCode(listPaasResult.getCode());
        result.setMessage(listPaasResult.getMessage());

        if(CollectionUtils.isNotEmpty(listPaasResult.getData())) {
            List<PlatformIndexData> indicatrixResponseVals = new ArrayList<>(listPaasResult.getData().size());
            PlatformIndexData responseVal = null;
            for (GaeaIndicatrixVal datum : listPaasResult.getData()) {
                responseVal = new PlatformIndexData();
                responseVal.setIndicatrixId(datum.getIndicatrixId());

                responseVal.setDetail(datum.getConditionDetail());

                responseVal.setMeaningCode(datum.getMeaningCode());
                responseVal.setDetailCode(datum.getDetailCode());
                responseVal.setValue(datum.getResult());
                responseVal.setRetCode(datum.getRetCode());
                responseVal.setStringValue(datum.getStrResult());
                responseVal.setOriginalValue(datum.getResult());

                indicatrixResponseVals.add(responseVal);
            }
            result.setData(indicatrixResponseVals);
        }

        return result;
    }

    @Override
    protected String getApiTag() {
        return "paas.api.GaeaApi";
    }
}
