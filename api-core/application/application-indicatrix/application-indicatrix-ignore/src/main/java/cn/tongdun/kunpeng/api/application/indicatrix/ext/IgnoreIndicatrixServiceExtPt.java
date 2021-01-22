package cn.tongdun.kunpeng.api.application.indicatrix.ext;

import cn.tongdun.kunpeng.api.application.intf.IndicatrixServiceExtPt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;

/**
 * 北美 指标平台扩展点实现
 * @author jie
 * @date 2020/12/15
 */
@Extension(tenant = BizScenario.DEFAULT,business = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class IgnoreIndicatrixServiceExtPt implements IndicatrixServiceExtPt {

    private static final Logger logger = LoggerFactory.getLogger(IgnoreIndicatrixServiceExtPt.class);

    @Override
    public boolean calculate(AbstractFraudContext fraudContext) {

        return true;
    }
}
