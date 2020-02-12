package cn.tongdun.kunpeng.api.engine.model.rule.function.android;

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
//    "name": "packageName",
//    "type": "string",
//    "value": "com.example.fmdemo207"
//  }
//]

    private String packageName;


    @Override
    public String getName() {
        return Constant.Function.ANDROID_NOT_OFFICIAL_APP;
    }

    @Override
    public void parse(List<FunctionParam> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.forEach(functionParam -> {
            if (StringUtils.equals("packageName", functionParam.getName())) {
                packageName = functionParam.getValue();
            }
        });
    }

    @Override
    public CalculateResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        boolean ret = false;
        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            ret = false;
        }
        else {
            Object officialPackageName = deviceInfo.get("packageName");
            if (officialPackageName != null && !officialPackageName.equals(packageName)) {

                ret = true;
            }
            else {
                ret = false;
            }
        }

        return new CalculateResult(ret, null);
    }
}
