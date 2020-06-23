package cn.tongdun.kunpeng.api.basedata.rule.function.android;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.ruledetail.UseVpnDetail;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class AndroidUseVpnFunction extends AbstractFunction {


    @Override
    public String getName() {
        return Constant.Function.ANDROID_VPN;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {

    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

        boolean ret = false;
        DetailCallable detailCallable = null;
        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            ret = false;
        }
        else {
            Object isUseVpn = deviceInfo.get("vpnIp");
            if (isUseVpn != null && StringUtils.isNotBlank(isUseVpn.toString())) {
                ret = true;
                detailCallable = ()->{
                    UseVpnDetail useVpnDetail = new UseVpnDetail();
                    useVpnDetail.setVpnIp(isUseVpn.toString());
                    useVpnDetail.setConditionUuid(this.conditionUuid);
                    useVpnDetail.setRuleUuid(this.ruleUuid);
                    useVpnDetail.setDescription(this.description);
                    return useVpnDetail;
                };
            }
            else {
                ret = false;
            }
        }
        return new FunctionResult(ret, detailCallable);
    }


}
