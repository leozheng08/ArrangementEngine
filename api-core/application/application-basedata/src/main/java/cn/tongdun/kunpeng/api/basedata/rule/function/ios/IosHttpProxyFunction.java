package cn.tongdun.kunpeng.api.basedata.rule.function.ios;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.ruledetail.IOSUseHttpProxyDetail;
import cn.tongdun.kunpeng.api.ruledetail.UseHttpProxyDetail;

import java.util.Collection;
import java.util.Map;

public class IosHttpProxyFunction extends AbstractFunction {

    @Override
    public String getName() {
        return Constant.Function.IOS_USE_HTTP_PROXY;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {

    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            return new FunctionResult(false);
        }
        else {
            /**
             * 设备指纹类规则模版优化：详见：http://wiki.tongdun.me/pages/viewpage.action?pageId=46454996
             */
            Collection<String> fpAbnormalTags = (Collection<String>) deviceInfo.get("abnormalTags");

            // 防止空指针异常
            if(fpAbnormalTags == null){
                return new FunctionResult(false);
            }

            boolean result = fpAbnormalTags.contains("PROXY_DETECTED");

            Object proxyType = deviceInfo.get("proxyType");

            if (result) {
                DetailCallable detailCallable = () -> {
                    UseHttpProxyDetail useHttpProxyDetail = new UseHttpProxyDetail();
                    useHttpProxyDetail.setConditionUuid(this.getConditionUuid());
                    useHttpProxyDetail.setRuleUuid(this.getRuleUuid());
                    useHttpProxyDetail.setDescription(this.getDescription());
                    useHttpProxyDetail.setProxyType(proxyType != null ? proxyType.toString() : null);
                    return useHttpProxyDetail;
                };
                return new FunctionResult(true, detailCallable);
            }
            else {
                return new FunctionResult(false);
            }
        }
    }


}
