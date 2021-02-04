package cn.tongdun.kunpeng.api.application.indicatrix.ext;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.tongdun.kunpeng.api.application.intf.IndicatrixServiceExtPt;
import cn.tongdun.kunpeng.api.application.intf.KpIndicatrixService;
import cn.tongdun.kunpeng.api.application.pojo.IndicatrixApiResult;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.PlatformIndexData;
import cn.tongdun.tdframework.core.extension.Extension;

/**
 * 北美 指标平台扩展点实现
 * @author jie
 * @date 2020/12/15
 */
@Extension(tenant = "us",business = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class UsIndicatrixServiceExtPt implements IndicatrixServiceExtPt {

    private static final Logger logger = LoggerFactory.getLogger(UsIndicatrixServiceExtPt.class);

    @Autowired
    private KpIndicatrixService kpIndicatrixService;

    @Override
    public boolean calculate(AbstractFraudContext fraudContext) {

        IndicatrixApiResult<List<PlatformIndexData>> indicatrixApiResult = kpIndicatrixService.calculateByIdsAndSetContext(fraudContext);

        if (null == indicatrixApiResult) {
            logger.warn("UsIndicatrixServiceExtPt calculate result is null");
            return false;
        }

        logger.info("UsIndicatrixServiceExtPt calculate result:{},code:{}", indicatrixApiResult.isSuccess(), indicatrixApiResult.getCode());
        return indicatrixApiResult.isSuccess();
    }
}
