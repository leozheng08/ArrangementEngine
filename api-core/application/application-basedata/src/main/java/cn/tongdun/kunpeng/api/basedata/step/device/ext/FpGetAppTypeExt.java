package cn.tongdun.kunpeng.api.basedata.step.device.ext;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;

/**
 * @Author: liang.chen
 * @Date: 2020/2/10 下午9:44
 */
@Extension(tenant = BizScenario.DEFAULT,business = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class FpGetAppTypeExt implements IFpGetAppTypeExtPt {

    @Override
    public String getAppType(AbstractFraudContext context){
        //todo
        return "ios";
    }
}
