package cn.tongdun.kunpeng.api.application.platformindex.impl;

import cn.tongdun.kunpeng.api.application.platformindex.pojo.IndicatrixApiResult;
import cn.tongdun.kunpeng.api.application.platformindex.pojo.IndicatrixRequest;
import cn.tongdun.kunpeng.api.common.data.PlatformIndexData;
import cn.tongdun.shenwei.client.ShenWeiUsApi;
import cn.tongdun.shenwei.dto.ShenWeiIndicatrixQuery;
import cn.tongdun.shenwei.dto.ShenWeiIndicatrixVal;
import cn.tongdun.shenwei.dto.ShenWeiResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 指标平台 shenwei client  对接
 * @author jie
 * @date 2020/12/30
 */
public class ShenweiServiceImpl extends AbstractKpIndicatrixService<ShenWeiResult<List<ShenWeiIndicatrixVal>>>{

    @Autowired
    private ShenWeiUsApi shenWeiUsApi;

    public ShenweiServiceImpl(ShenWeiUsApi shenWeiUsApi) {
        this.shenWeiUsApi = shenWeiUsApi;
    }

    @Override
    protected String getApiTag() {
        return "shenwei.client";
    }

    @Override
    protected IndicatrixApiResult<List<PlatformIndexData>> convertApiResult(ShenWeiResult<List<ShenWeiIndicatrixVal>>  apiResult) {
        IndicatrixApiResult<List<PlatformIndexData>> result = null;
        if (null == apiResult) {
            return null;
        }

        result = new IndicatrixApiResult<>();
        result.setSuccess(apiResult.isSuccess());
        result.setCode(apiResult.getCode());
        result.setMessage(apiResult.getMessage());

        if(CollectionUtils.isNotEmpty(apiResult.getData())) {
            List<PlatformIndexData> indicatrixResponseVals = new ArrayList<>(apiResult.getData().size());
            PlatformIndexData responseVal = null;
            for (ShenWeiIndicatrixVal datum : apiResult.getData()) {
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
    public IndicatrixApiResult<List<PlatformIndexData>> calculateByIds(IndicatrixRequest indicatrixRequest) throws Exception {
        ShenWeiIndicatrixQuery indicatrixValQuery = new ShenWeiIndicatrixQuery();
        indicatrixValQuery.setBizId(indicatrixRequest.getBizId());
        indicatrixValQuery.setPartnerCode(indicatrixRequest.getPartnerCode());
        indicatrixValQuery.setEventType(indicatrixRequest.getEventType());
        indicatrixValQuery.setEventId(indicatrixRequest.getEventId());
        indicatrixValQuery.setAppName(indicatrixRequest.getAppName());
        indicatrixValQuery.setActivity(indicatrixRequest.getActivity());
        indicatrixValQuery.setEventOccurTime(indicatrixRequest.getEventOccurTime());
        indicatrixValQuery.setIndicatrixIds(indicatrixRequest.getIndicatrixIds());
        indicatrixValQuery.setNeedDetail(true);

        ShenWeiResult<List<ShenWeiIndicatrixVal>> indicatrixResult = shenWeiUsApi.calcMulti(indicatrixValQuery);

        return convertApiResult(indicatrixResult);
    }
}
