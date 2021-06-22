package cn.tongdun.kunpeng.api.application.indicatrix.impl;

import cn.tongdun.gaea.client.common.IndicatrixParam;
import cn.tongdun.gaea.client.common.IndicatrixParamBuilder;
import cn.tongdun.gaea.client.common.IndicatrixResult;
import cn.tongdun.gaea.client.common.base.Result;
import cn.tongdun.kunpeng.api.application.intf.AbstractKpIndicatrixService;
import cn.tongdun.kunpeng.api.application.pojo.IndicatrixApiResult;
import cn.tongdun.kunpeng.api.application.pojo.IndicatrixRequest;
import cn.tongdun.kunpeng.api.common.data.PlatformIndexData;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * gaea-api-client saas国内 印尼 指标接口公共抽象类
 *
 * @author jie
 * @date 2020/12/15
 */
@Component
public abstract class AbstractGaeaApiKpIndicatrixService extends AbstractKpIndicatrixService<Result<List<IndicatrixResult>>> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractGaeaApiKpIndicatrixService.class);

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

        if (CollectionUtils.isNotEmpty(listResult.getVal())) {
            List<PlatformIndexData> indicatrixResponseVals = new ArrayList<>(listResult.getVal().size());
            PlatformIndexData responseVal = null;
            for (IndicatrixResult iResult : listResult.getVal()) {
                responseVal = new PlatformIndexData();
                responseVal.setIndicatrixId(iResult.getIndicatrixId());
                responseVal.setDesc(iResult.getDescription());
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
