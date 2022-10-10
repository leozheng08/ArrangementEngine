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

import java.util.Collection;
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
            /**
             * 设备指纹类规则模版优化：详见：http://wiki.tongdun.me/pages/viewpage.action?pageId=46454996
             */
            Collection<String> fpAbnormalTags = (Collection<String>) deviceInfo.get("abnormalTags");
            boolean result = fpAbnormalTags.contains("VPN_DETECTED");

            Object vpnIp = deviceInfo.get("vpnIp");

            if (result) {
                DetailCallable detailCallable = ()->{
                    UseVpnDetail useVpnDetail = new UseVpnDetail();
                    useVpnDetail.setVpnIp(vpnIp != null ? vpnIp.toString() : null);
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
