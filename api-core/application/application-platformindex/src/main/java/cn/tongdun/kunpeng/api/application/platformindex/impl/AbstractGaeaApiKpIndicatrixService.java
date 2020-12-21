package cn.tongdun.kunpeng.api.application.platformindex.impl;

import cn.tongdun.gaea.client.common.IndicatrixParam;
import cn.tongdun.gaea.client.common.IndicatrixParamBuilder;
import cn.tongdun.gaea.client.common.IndicatrixResult;
import cn.tongdun.gaea.client.common.base.Result;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * gaea-api-client saas国内 印尼 指标接口公共抽象类
 * @author jie
 * @date 2020/12/15
 */
@Component
public abstract class AbstractGaeaApiKpIndicatrixService extends AbstractKpIndicatrixService<Result<List<IndicatrixResult>>> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractGaeaApiKpIndicatrixService.class);

    @Autowired
    private PlatformIndexCache policyIndicatrixItemCache;

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

        // 仿真、试运行等印尼先不考虑
        IndicatrixRequest indicatrixRequest = new IndicatrixRequest();
        indicatrixRequest.setBizId(context.getSeqId());
        indicatrixRequest.setPartnerCode(context.getPartnerCode());
        indicatrixRequest.setEventType(context.getEventType());
        indicatrixRequest.setEventId(context.getEventId());
        indicatrixRequest.setAppName(context.getAppName());
        indicatrixRequest.setActivity(activityParam);
        indicatrixRequest.setTestFlag(context.isTestFlag());
        indicatrixRequest.setEventOccurTime(context.getEventOccurTime().getTime());
        indicatrixRequest.setIndicatrixIds(indicatrixsParam);
        indicatrixRequest.setNeedDetail(true);

        return indicatrixRequest;
    }

    protected IndicatrixParam convertRequest2Param(IndicatrixRequest indicatrixRequest) {
        IndicatrixParam indicatrixParam = new IndicatrixParamBuilder()
                .setBizName(indicatrixRequest.getBizName())
                .setBizId(indicatrixRequest.getBizId())
                .setPartnerCode(indicatrixRequest.getPartnerCode())
                .setIndicatrixIds(indicatrixRequest.getIndicatrixIds())
                .setAppName(indicatrixRequest.getAppName())
                .setTestFlag(indicatrixRequest.isTestFlag())
                .setEventType(indicatrixRequest.getEventType())
                .setEventId(indicatrixRequest.getEventId())
                .setNeedIndicatrixDefineInfo(true)
                .setBizParam(indicatrixRequest.getActivity())
                .setEventOccurTime(indicatrixRequest.getEventOccurTime())
                .buildByIndicatrixId();

        return indicatrixParam;
    }

    @Override
    protected IndicatrixApiResult<List<PlatformIndexData>> convertApiResult(Result<List<IndicatrixResult>> listResult) {
        IndicatrixApiResult<List<PlatformIndexData>> result = null;
        if (null == listResult) {
        return null;
        }

        result = new IndicatrixApiResult<>();
        result.setSuccess(listResult.isSuccess());

        if(CollectionUtils.isNotEmpty(listResult.getVal())){
        List<PlatformIndexData> indicatrixResponseVals = new ArrayList<>(listResult.getVal().size());
        PlatformIndexData responseVal = null;
        for (IndicatrixResult iResult : listResult.getVal()) {
        responseVal = new PlatformIndexData();
        responseVal.setIndicatrixId(iResult.getIndicatrixId());
        responseVal.setDescription(iResult.getDescription());
        responseVal.setDetail(iResult.getConditionDetail());

        responseVal.setFeatureLevel1Name(iResult.getFeatureLevel1Name());
        responseVal.setFeatureLevel2Name(iResult.getFeatureLevel2Name());
        responseVal.setFeatureLevel3Name(iResult.getFeatureLevel3Name());
        responseVal.setMeaningCode(iResult.getMeaningCode());
        responseVal.setRetCode(iResult.getReasonCode());
        responseVal.setValue(parseDouble(iResult.getResult()));

        indicatrixResponseVals.add(responseVal);
        }

        result.setData(indicatrixResponseVals);
        }

        return result;
    }
}
