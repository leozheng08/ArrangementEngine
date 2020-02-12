package cn.tongdun.kunpeng.api.engine.model.rule.function.android;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class Root extends AbstractFunction {

    @Override
    public String getName() {
        return Constant.Function.ANDROID_ROOT;
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

        Object isRoot = deviceInfo.get("isRoot");
        Object isRootNew = deviceInfo.get("root");
        if (isRoot != null && StringUtils.equalsIgnoreCase(isRoot.toString(), "true")) {
            return new CalculateResult(true, null);
        }

        if (isRootNew != null && Boolean.TRUE.equals(isRootNew)) {
            return new CalculateResult(true, null);
        }

        return new CalculateResult(false, null);
    }
}
