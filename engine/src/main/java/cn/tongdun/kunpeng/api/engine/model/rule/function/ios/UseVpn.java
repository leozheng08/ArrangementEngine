package cn.tongdun.kunpeng.api.engine.model.rule.function.ios;

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
        return Constant.Function.IOS_USE_VPN;
    }

    @Override
    public void parse(List<FunctionParam> functionParamList) {

    }

    @Override
    public CalculateResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            return new CalculateResult(false, null);
        }
        else {
            Object vpnIp = deviceInfo.get("vpnIp");
            if (vpnIp != null && StringUtils.isNotBlank(vpnIp.toString())) {
                return new CalculateResult(true, null);
            }
            else {
                return new CalculateResult(false, null);
            }
        }
    }
}
