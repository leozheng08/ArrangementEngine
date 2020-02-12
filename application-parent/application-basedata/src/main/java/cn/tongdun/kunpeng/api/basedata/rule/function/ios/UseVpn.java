package cn.tongdun.kunpeng.api.basedata.rule.function.ios;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class UseVpn extends AbstractFunction {


    @Override
    public String getName() {
        return Constant.Function.IOS_USE_VPN;
    }


    @Override
    public void parse(FunctionDesc functionDesc) {

    }

    @Override
    public Object eval(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            return false;
        }
        else {
            Object vpnIp = deviceInfo.get("vpnIp");
            if (vpnIp != null && StringUtils.isNotBlank(vpnIp.toString())) {
                return true;
            }
            else {
                return false;
            }
        }
    }


}
