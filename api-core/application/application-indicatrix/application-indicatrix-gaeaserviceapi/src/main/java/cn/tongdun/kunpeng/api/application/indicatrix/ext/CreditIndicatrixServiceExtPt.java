package cn.tongdun.kunpeng.api.application.indicatrix.ext;

import cn.tongdun.kunpeng.api.application.intf.IndicatrixServiceExtPt;
import cn.tongdun.kunpeng.api.application.intf.KpIndicatrixService;
import cn.tongdun.kunpeng.api.application.pojo.IndicatrixApiResult;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.PlatformIndexData;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.extension.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 * 国内/印尼版 信贷场景 指标平台扩展点
 * @author jie
 * @date 2020/12/14
 */
@Extension(tenant = BizScenario.DEFAULT,business = "credit",partner = BizScenario.DEFAULT)
public class CreditIndicatrixServiceExtPt implements IndicatrixServiceExtPt {

    private static final Logger logger = LoggerFactory.getLogger(CreditIndicatrixServiceExtPt.class);

    @Resource(name = "gaeaApiAbnormalService")
    private KpIndicatrixService kpIndicatrixService;

    @Override
    public boolean calculate(AbstractFraudContext fraudContext) {
        IndicatrixApiResult<List<PlatformIndexData>> apiResult = kpIndicatrixService.calculateByIdsAndSetContext(fraudContext);

        if (null == apiResult) {
            logger.debug("SaasIndicatrixCreditServiceExtPt calculate result is null");
            return true;
        }

        logger.info(TraceUtils.getFormatTrace() + "SaasIndicatrixCreditServiceExtPt calculate result:{},code:{}", apiResult.isSuccess(), apiResult.getCode());
        return true;
    }
}