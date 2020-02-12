package cn.tongdun.kunpeng.api.engine.model.rule.function.android;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class UseVpn extends AbstractFunction {


    @Override
    public String getName() {
        return Constant.Function.ANDROID_VPN;
    }

    @Override
    public void parse(List<FunctionParam> functionParamList) {

    }

    @Override
    public CalculateResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        boolean ret = false;
        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            ret = false;
        }
        else {
            Object isUseVpn = deviceInfo.get("vpnIp");
            if (isUseVpn != null && StringUtils.isNotBlank(isUseVpn.toString())) {
                ret = true;
            }
            else {
                ret = false;
            }
        }
        return new CalculateResult(ret, null);
    }
}
