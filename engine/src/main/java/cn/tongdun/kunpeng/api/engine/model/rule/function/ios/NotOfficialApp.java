package cn.tongdun.kunpeng.api.engine.model.rule.function.ios;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class NotOfficialApp extends AbstractFunction {
//[
//  {
//    "name": "bundleId",
//    "type": "string",
//    "value": "1111"
//  }
//]

    private String bundleId;


    @Override
    public String getName() {
        return Constant.Function.IOS_NOT_OFFICIAL_APP;
    }

    @Override
    public void parse(List<FunctionParam> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.forEach(functionParam -> {
            if (StringUtils.equals("bundleId", functionParam.getName())) {
                bundleId = functionParam.getValue();
            }
        });
    }

    @Override
    public CalculateResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            return new CalculateResult(false, null);
        }
        else {
            Object officialBundleId = deviceInfo.get("bundleId");
            if (officialBundleId != null && !officialBundleId.equals(bundleId)) {
                return new CalculateResult(true, null);
            }
            else {
                return new CalculateResult(false, null);
            }
        }
    }
}
