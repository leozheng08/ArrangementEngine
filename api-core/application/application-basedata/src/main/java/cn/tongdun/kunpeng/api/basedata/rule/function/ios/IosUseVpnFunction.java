package cn.tongdun.kunpeng.api.basedata.rule.function.ios;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.ruledetail.UseVpnDetail;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class IosUseVpnFunction extends AbstractFunction {


    @Override
    public String getName() {
        return Constant.Function.IOS_USE_VPN;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {

    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            return new FunctionResult(false);
        }
        else {
            Object vpnIp = deviceInfo.get("vpnIp");
            if (vpnIp != null && StringUtils.isNotBlank(vpnIp.toString())) {
                DetailCallable detailCallable = ()->{
                    UseVpnDetail useVpnDetail = new UseVpnDetail();
                    useVpnDetail.setVpnIp(vpnIp.toString());
                    useVpnDetail.setConditionUuid(this.conditionUuid);
                    useVpnDetail.setRuleUuid(this.ruleUuid);
                    useVpnDetail.setDescription(this.description);
                    return useVpnDetail;
                };
                return new FunctionResult(true, detailCallable);
            }
            else {
                return new FunctionResult(false);
            }
        }
    }


}
