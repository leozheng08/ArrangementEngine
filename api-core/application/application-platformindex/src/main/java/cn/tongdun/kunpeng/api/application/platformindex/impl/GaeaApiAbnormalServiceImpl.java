package cn.tongdun.kunpeng.api.application.platformindex.impl;

import cn.tongdun.gaea.api.client.IndicatrixApi;
import cn.tongdun.gaea.client.common.IndicatrixParam;
import cn.tongdun.gaea.client.common.IndicatrixResult;
import cn.tongdun.gaea.client.common.base.Result;
import cn.tongdun.kunpeng.api.application.platformindex.pojo.IndicatrixApiResult;
import cn.tongdun.kunpeng.api.application.platformindex.pojo.IndicatrixRequest;
import cn.tongdun.kunpeng.api.common.data.PlatformIndexData;

import java.util.List;

/**
 * gaea-api-client 异常敏感型 指标接口对接实现
 *  国内、印尼使用
 * @author jie
 * @date 2020/12/14
 */
public class GaeaApiAbnormalServiceImpl extends AbstractGaeaApiKpIndicatrixService {

    private IndicatrixApi indicatrixApi;

    public GaeaApiAbnormalServiceImpl(IndicatrixApi indicatrixApi) {
        this.indicatrixApi = indicatrixApi;
    }

    @Override
    public IndicatrixApiResult<List<PlatformIndexData>> calculateByIds(IndicatrixRequest indicatrixRequest) {
        // 1. 转换请求参数
        IndicatrixParam indicatrixParam = convertRequest2Param(indicatrixRequest);

        IndicatrixApiResult<List<PlatformIndexData>> result = null;

        // 2. 根据指标ID计算,适用于延迟敏感型场景(p999 50ms)
        Result<List<IndicatrixResult>> apiResult = indicatrixApi.calculateByIdForAbnormalSensitive(indicatrixParam);

        return convertApiResult(apiResult);
    }

    @Override
    protected String getApiTag() {
        return "saas.api.indicatrixApi.abnormal";
    }
}
