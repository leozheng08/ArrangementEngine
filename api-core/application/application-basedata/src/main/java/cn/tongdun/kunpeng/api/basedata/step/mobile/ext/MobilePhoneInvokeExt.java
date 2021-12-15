package cn.tongdun.kunpeng.api.basedata.step.mobile.ext;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.Extension;

/**
 * @Author: liang.chen
 * @Date: 2020/2/10 下午9:44
 */
@Extension(business = BizScenario.DEFAULT, tenant = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class MobilePhoneInvokeExt implements IMobilePhoneInvokeExtPt {

    @Override
    public boolean fetchData(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        return true;
    }
}
