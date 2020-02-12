package cn.tongdun.kunpeng.api.basedata.rule.function.ios;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.common.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Cheat extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(Cheat.class);


    @Override
    public String getName() {
        return Constant.Function.IOS_IS_CHEAT;
    }


    @Override
    public void parse(FunctionDesc functionDesc) {

    }

    @Override
    public Object eval(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

        Map<String, Object> systemFields = context.getSystemFiels();
        Object containsCheatApp = systemFields.get("containsCheatApp");
        if (containsCheatApp == null) {
            logger.warn("ios cheat containsCheatApp system field not configured");
            return false;
        }
        else {
            if (Boolean.TRUE.equals(containsCheatApp)) {
                return true;
            }
            else {
                return false;
            }
        }
    }


}
