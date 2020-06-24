package cn.tongdun.kunpeng.api.basedata.rule.function.ios;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.ruledetail.IOSNotOfficialAppDetail;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class IosNotOfficialAppFunction extends AbstractFunction {

    private String bundleId;


    @Override
    public String getName() {
        return Constant.Function.IOS_NOT_OFFICIAL_APP;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {
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
    public FunctionResult run(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            return new FunctionResult(false);
        }
        else {
            Object officialBundleId = deviceInfo.get("bundleId");
            if (officialBundleId != null && !officialBundleId.equals(bundleId)) {
                DetailCallable detailCallable = ()->{
                    IOSNotOfficialAppDetail detail = new IOSNotOfficialAppDetail();
                    detail.setBundleId(bundleId);
                    detail.setConditionUuid(this.getConditionUuid());
                    detail.setRuleUuid(this.getRuleUuid());
                    detail.setDescription(this.getDescription());
                    return detail;
                };
                return new FunctionResult(true, detailCallable);
            }
            else {
                return new FunctionResult(false);
            }
        }
    }


}
