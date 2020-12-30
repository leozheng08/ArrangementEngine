package cn.tongdun.kunpeng.api.application.platformindex.impl;

import cn.tongdun.gaea.paas.api.GaeaApi;
import cn.tongdun.gaea.paas.dto.GaeaIndicatrixVal;
import cn.tongdun.gaea.paas.dto.IndicatrixValQuery;
import cn.tongdun.gaea.paas.dto.PaasResult;
import cn.tongdun.kunpeng.api.application.platformindex.pojo.IndicatrixApiResult;
import cn.tongdun.kunpeng.api.application.platformindex.pojo.IndicatrixRequest;
import cn.tongdun.kunpeng.api.common.data.PlatformIndexData;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.PlatformIndexCache;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 北美指标平台对接
 * @author jie
 * @date 2020/12/14
 */
public class GaeaPaasServiceImpl extends AbstractKpIndicatrixService<PaasResult<List<GaeaIndicatrixVal>>> {

    private static final Logger logger = LoggerFactory.getLogger(GaeaPaasServiceImpl.class);

    private GaeaApi gaeaApi;

    public GaeaPaasServiceImpl(GaeaApi gaeaApi) {
        this.gaeaApi = gaeaApi;
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
