package cn.tongdun.kunpeng.api.basedata.fp.ext;

import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;

/**
 * @Author: liang.chen
 * @Date: 2020/2/10 下午9:44
 */
@Extension(business = BizScenario.DEFAULT_BUSINESS,tenant = BizScenario.DEFAULT_BUSINESS,partner = BizScenario.DEFAULT_PARTNER)
public class FpGetAppTypeExt implements IFpGetAppTypeExtPt {

    @Override
    public String getAppType(AbstractFraudContext context){
        //todo
        return "ios";
    }
}
