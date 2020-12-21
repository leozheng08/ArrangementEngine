package cn.tongdun.kunpeng.api.application.platformindex;

import cn.tongdun.kunpeng.api.application.platformindex.pojo.IndicatrixApiResult;
import cn.tongdun.kunpeng.api.application.platformindex.pojo.IndicatrixRequest;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.PlatformIndexData;

import java.util.List;

/**
 * 指标接口对接适配接口
 *     @Resource name引用根据场景使用以下不同实现
 *     paasGaeaService: gaea-paas-client 对接方式（北美）
 *     gaeaApiAbnormalService: gaea-api-cliet 对接方式（国内、印尼）异常敏感型、信贷场景
 *     gaeaApiLatencyService: gaea-api-client 对接方式（国内、印尼）rt敏感型、信贷场景
 * @author jie
 * @date 2020/12/14
 */
public interface KpIndicatrixService {

    /**
     * 指标结果查询接口
     * @param indicatrixRequest
     * @return
     * @throws Exception
     */
    IndicatrixApiResult<List<PlatformIndexData>> calculateByIds(IndicatrixRequest indicatrixRequest) throws Exception;

    /**
     * 指标结果查询接口
     * @param context
     * @return
     */
    IndicatrixApiResult<List<PlatformIndexData>> calculateByIdsAndSetContext(AbstractFraudContext context);
}
