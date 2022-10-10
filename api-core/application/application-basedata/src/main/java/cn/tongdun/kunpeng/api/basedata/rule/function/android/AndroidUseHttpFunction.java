package cn.tongdun.kunpeng.api.basedata.rule.function.android;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.ruledetail.UseHttpProxyDetail;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;

public class AndroidUseHttpFunction extends AbstractFunction {

    @Override
    public String getName() {
        return Constant.Function.ANDROID_HTTP;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {

    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

        boolean result = false;
        DetailCallable detailCallable = null;
        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null) {
            result = false;
        } else {
            /**
             * 设备指纹类规则模版优化：详见：http://wiki.tongdun.me/pages/viewpage.action?pageId=46454996
             */
            Collection<String> fpAbnormalTags = (Collection<String>) deviceInfo.get("abnormalTags");
            result = fpAbnormalTags.contains("PROXY_DETECTED");

            Object isUseHttpProxy = deviceInfo.get("proxyStr");
            Object isUseHttpProxyNew = deviceInfo.get("proxyInfo");

            if (isUseHttpProxy != null && StringUtils.isNotBlank(isUseHttpProxy.toString()) && result) {
                detailCallable = genDetailCallable(isUseHttpProxy.toString());
            } else if (isUseHttpProxyNew != null && StringUtils.isNotBlank(isUseHttpProxyNew.toString()) && result) {
                detailCallable = genDetailCallable(isUseHttpProxyNew.toString());
            }
        }

        return new FunctionResult(result, detailCallable);
    }

    private DetailCallable genDetailCallable(String proxyType){
        DetailCallable detailCallable = () -> {
            UseHttpProxyDetail useHttpProxyDetail = new UseHttpProxyDetail();
            useHttpProxyDetail.setConditionUuid(this.getConditionUuid());
            useHttpProxyDetail.setRuleUuid(this.getRuleUuid());
            useHttpProxyDetail.setDescription(this.getDescription());
            useHttpProxyDetail.setProxyType(proxyType.toString());
            return useHttpProxyDetail;
        };
        return detailCallable;
    }


}
