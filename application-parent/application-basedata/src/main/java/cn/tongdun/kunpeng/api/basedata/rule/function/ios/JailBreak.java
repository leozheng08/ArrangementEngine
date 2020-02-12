package cn.tongdun.kunpeng.api.basedata.rule.function.ios;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.common.Constant;

import java.util.Map;

public class JailBreak extends AbstractFunction {


    @Override
    public String getName() {
        return Constant.Function.IOS_IS_JAILBREAK;
    }


    @Override
    public void parse(FunctionDesc functionDesc) {

    }

    @Override
    public Object eval(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            return false;
        }
        else {
            Object jailBreak = deviceInfo.get("jailbreak");
            String appType = context.getAppType();
            if ("ios".equalsIgnoreCase(appType) && "1".equals(jailBreak)) {
                return true;
            }
            else {
                return false;
            }
        }
    }


}
