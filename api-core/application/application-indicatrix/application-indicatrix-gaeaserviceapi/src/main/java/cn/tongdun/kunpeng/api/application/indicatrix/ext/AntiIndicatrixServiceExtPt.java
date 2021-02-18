package cn.tongdun.kunpeng.api.application.indicatrix.ext;


import cn.tongdun.kunpeng.api.application.intf.IndicatrixServiceExtPt;
import cn.tongdun.kunpeng.api.application.intf.KpIndicatrixService;
import cn.tongdun.kunpeng.api.application.pojo.IndicatrixApiResult;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.PlatformIndexData;
import cn.tongdun.tdframework.core.extension.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 * 国内/印尼版 反欺诈场景(延时敏感) 指标平台扩展点
 * @author jie
 * @date 2020/12/14
 */
@Extension(tenant = BizScenario.DEFAULT,business = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class AntiIndicatrixServiceExtPt implements IndicatrixServiceExtPt {

    private static final Logger logger = LoggerFactory.getLogger(AntiIndicatrixServiceExtPt.class);

    @Resource(name = "gaeaApiLatencyService")
    private KpIndicatrixService kpIndicatrixService;

    @Override
    public boolean calculate(AbstractFraudContext fraudContext) {
        IndicatrixApiResult<List<PlatformIndexData>> apiResult = kpIndicatrixService.calculateByIdsAndSetContext(fraudContext);

        if (null == apiResult) {
            logger.warn("AntiIndicatrixServiceExtPt calculate result is null");
            return false;
        }

        logger.info("AntiIndicatrixServiceExtPt calculate result:{},code:{}", apiResult.isSuccess(), apiResult.getCode());
        return apiResult.isSuccess();
    }
}
