package cn.tongdun.kunpeng.api.basedata.rule.function.ios;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class NotOfficialApp extends AbstractFunction {

    private String bundleId;


    @Override
    public String getName() {
        return Constant.Function.IOS_NOT_OFFICIAL_APP;
    }


    @Override
    public void parse(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("ios NotOfficialApp function parse error,no params!");
        }

        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("bundleId", param.getName())) {
                bundleId = param.getValue();
            }
        });
    }

    @Override
    public Object eval(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            return false;
        }
        else {
            Object officialBundleId = deviceInfo.get("bundleId");
            if (officialBundleId != null && !officialBundleId.equals(bundleId)) {
                return true;
            }
            else {
                return false;
            }
        }
    }


}
