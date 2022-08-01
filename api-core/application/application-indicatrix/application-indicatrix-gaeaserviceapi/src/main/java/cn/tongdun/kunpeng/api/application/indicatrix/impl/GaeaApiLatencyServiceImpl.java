package cn.tongdun.kunpeng.api.application.indicatrix.impl;

import cn.tongdun.gaea.client.common.IndicatrixParam;
import cn.tongdun.gaea.client.common.IndicatrixResult;
import cn.tongdun.gaea.client.common.base.Result;
import cn.tongdun.kunpeng.api.application.pojo.IndicatrixApiResult;
import cn.tongdun.kunpeng.api.application.pojo.IndicatrixRequest;
import cn.tongdun.kunpeng.api.common.data.PlatformIndexData;
import cn.tongdun.kunpeng.api.engine.model.dictionary.DictionaryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${indicatrix.source.type:gaea}")
    private String indicatrixSourceType;
    @Autowired
    private cn.tongdun.shenwei.gateway.client.IndicatrixApi indicatrixApiGaea;
    @Autowired
    private cn.tongdun.gaea.api.client.IndicatrixApi indicatrixApiShenwei;

    @Autowired
    private DictionaryManager dictionaryManager;

    @Override
    public IndicatrixApiResult<List<PlatformIndexData>> calculateByIds(IndicatrixRequest indicatrixRequest) throws Exception {
        // 1. 转换请求参数
        IndicatrixParam indicatrixParam = convertRequest2Param(indicatrixRequest);

        // 2. 根据指标ID计算,适用于延迟敏感型场景(p999 50ms)
        Result<List<IndicatrixResult>> apiResult = null;

        // TODO 在指标拆分阶段暂存现象，根据配置，选择指标提供方是shenwei还是gaea,待指标流量都切到shenwei了，这里路由到gaea的逻辑可以直接删除了
        if(indicatrixSourceType.equals("shenwei")) {
            apiResult = indicatrixApiShenwei.calculateByIdForLatencySensitive(indicatrixParam);
        } else {
            apiResult = indicatrixApiGaea.calculateByIdForLatencySensitive(indicatrixParam);

        }

        return convertApiResult(apiResult);
    }

    @Override
    protected String getApiTag() {
        return "saas.api.indicatrixApi.latency";
    }

}
