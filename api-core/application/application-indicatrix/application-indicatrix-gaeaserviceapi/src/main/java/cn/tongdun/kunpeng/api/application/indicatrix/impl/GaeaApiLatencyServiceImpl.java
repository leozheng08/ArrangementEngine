package cn.tongdun.kunpeng.api.application.indicatrix.impl;

import cn.tongdun.gaea.api.client.IndicatrixApi;
import cn.tongdun.gaea.client.common.IndicatrixParam;
import cn.tongdun.gaea.client.common.IndicatrixResult;
import cn.tongdun.gaea.client.common.base.Result;
import cn.tongdun.kunpeng.api.application.pojo.IndicatrixApiResult;
import cn.tongdun.kunpeng.api.application.pojo.IndicatrixRequest;
import cn.tongdun.kunpeng.api.common.data.PlatformIndexData;
import cn.tongdun.kunpeng.api.engine.model.dictionary.DictionaryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * gaea-api-client rt敏感型场景 指标平台接口对接
 * 国内，印尼使用
 * @author jie
 * @date 2020/12/14
 */
@Service("gaeaApiLatencyService")
public class GaeaApiLatencyServiceImpl extends AbstractGaeaApiKpIndicatrixService {

    @Autowired
    private IndicatrixApi indicatrixApi;

    @Autowired
    private DictionaryManager dictionaryManager;

    @Override
    public IndicatrixApiResult<List<PlatformIndexData>> calculateByIds(IndicatrixRequest indicatrixRequest) throws Exception {
        // 1. 转换请求参数
        IndicatrixParam indicatrixParam = convertRequest2Param(indicatrixRequest);

        // 2. 根据指标ID计算,适用于延迟敏感型场景(p999 50ms)
        Result<List<IndicatrixResult>> apiResult = indicatrixApi.calculateByIdForLatencySensitive(indicatrixParam);
        return convertApiResult(apiResult);
    }

    @Override
    protected String getApiTag() {
        return "saas.api.indicatrixApi.latency";
    }

}
