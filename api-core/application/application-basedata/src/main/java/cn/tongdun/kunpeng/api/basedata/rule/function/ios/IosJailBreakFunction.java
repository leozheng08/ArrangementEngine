package cn.tongdun.kunpeng.api.basedata.rule.function.ios;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.ruledetail.IOSJailBreakDetail;

import java.util.Collection;
import java.util.Map;

public class IosJailBreakFunction extends AbstractFunction {


    @Override
    public String getName() {
        return Constant.Function.IOS_IS_JAILBREAK;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {

    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            return new FunctionResult(false);
        }
        else {
            /**
             * 设备指纹类规则模版优化：详见：http://wiki.tongdun.me/pages/viewpage.action?pageId=46454996
             */
            Collection<String> fpAbnormalTags = (Collection<String>) deviceInfo.get("abnormalTags");
            boolean result = fpAbnormalTags.contains("JAIL_BREAK");

            String appType = context.getAppType();
            if ("ios".equalsIgnoreCase(appType) && result) {
                DetailCallable detailCallable = ()->{
                    IOSJailBreakDetail iosJailBreakDetail = new IOSJailBreakDetail();
                    iosJailBreakDetail.setConditionUuid(this.getConditionUuid());
                    iosJailBreakDetail.setRuleUuid(this.getRuleUuid());
                    iosJailBreakDetail.setDescription(this.getDescription());
                    return iosJailBreakDetail;
                };
                return new FunctionResult(true, detailCallable);
            }
            else {
                return new FunctionResult(false);
            }
        }
    }


}
