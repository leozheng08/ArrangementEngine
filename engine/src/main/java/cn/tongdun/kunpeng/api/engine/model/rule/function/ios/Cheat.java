package cn.tongdun.kunpeng.api.engine.model.rule.function.ios;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class Cheat extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(Cheat.class);


    @Override
    public String getName() {
        return Constant.Function.IOS_IS_CHEAT;
    }

    @Override
    public void parse(List<FunctionParam> functionParamList) {

    }

    @Override
    public CalculateResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Map<String, Object> systemFields = context.getSystemFiels();
        Object containsCheatApp = systemFields.get("containsCheatApp");
        if (containsCheatApp == null) {
            logger.warn("ios cheat containsCheatApp system field not configured");
            return new CalculateResult(false, null);
        }
        else {
            if (Boolean.TRUE.equals(containsCheatApp)) {
                return new CalculateResult(true, null);
            }
            else {
                return new CalculateResult(false, null);
            }
        }
    }
}
