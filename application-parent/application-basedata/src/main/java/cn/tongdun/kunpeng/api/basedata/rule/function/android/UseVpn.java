package cn.tongdun.kunpeng.api.basedata.rule.function.android;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.common.Constant;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class UseVpn extends AbstractFunction {


    @Override
    public String getName() {
        return Constant.Function.ANDROID_VPN;
    }


    @Override
    public void parse(FunctionDesc functionDesc) {

    }

    @Override
    public Object eval(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

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
        return ret;
    }


}
