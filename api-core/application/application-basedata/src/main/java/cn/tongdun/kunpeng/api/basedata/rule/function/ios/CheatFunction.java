package cn.tongdun.kunpeng.api.basedata.rule.function.ios;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.ruledetail.IOSCheatAppDetail;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CheatFunction extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(CheatFunction.class);


    @Override
    public String getName() {
        return Constant.Function.IOS_IS_CHEAT;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {

    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

        Map<String, Object> fieldValues = context.getFieldValues();
        Object containsCheatApp = fieldValues.get("containsCheatApp");

        if (containsCheatApp == null) {
            logger.warn(TraceUtils.getFormatTrace()+"ios cheat containsCheatApp system field not configured");
            return new FunctionResult(false);
        }


        boolean ret = false;
        DetailCallable detailCallable = null;
        if (Boolean.TRUE.equals(containsCheatApp)) {
            ret = true;
        }

        if (ret && MapUtils.isNotEmpty(context.getDeviceInfo())) {
            detailCallable = () -> {
                IOSCheatAppDetail detail = new IOSCheatAppDetail();
                if (null != context.getDeviceInfo().get("hookInline")) {
                    detail.setHookInline(context.getDeviceInfo().get("hookInline").toString());
                }
                if (null != context.getDeviceInfo().get("hookIMP")) {
                    detail.setHookIMP(context.getDeviceInfo().get("hookIMP").toString());
                }
                return detail;
            };
        }
        return new FunctionResult(ret, detailCallable);
    }


}
