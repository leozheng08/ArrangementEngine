package cn.tongdun.kunpeng.api.basedata.rule.function.android;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.ruledetail.AndroidNotOfficialAppDetail;
import cn.tongdun.kunpeng.api.ruledetail.IOSNotOfficialAppDetail;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class AndroidNotOfficialAppFunction extends AbstractFunction {

    private String packageName;

    @Override
    public String getName() {
        return Constant.Function.ANDROID_NOT_OFFICIAL_APP;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("android NotOfficialApp function parse error,no params!");
        }

        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("packageName", param.getName())) {
                packageName = param.getValue();
            }
        });
    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

        boolean ret = false;
        DetailCallable detailCallable = null;
        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            ret = false;
        }
        else {
            Object officialPackageName = deviceInfo.get("packageName");
            if (officialPackageName != null && !officialPackageName.equals(packageName)) {
                detailCallable = ()->{
                    AndroidNotOfficialAppDetail detail = new AndroidNotOfficialAppDetail();
                    detail.setPackageName(packageName);
                    detail.setConditionUuid(this.getConditionUuid());
                    detail.setRuleUuid(this.getRuleUuid());
                    detail.setDescription(this.getDescription());
                    return detail;
                };
                ret = true;
            }
            else {
                ret = false;
            }
        }

        return new FunctionResult(ret, detailCallable);
    }


}
