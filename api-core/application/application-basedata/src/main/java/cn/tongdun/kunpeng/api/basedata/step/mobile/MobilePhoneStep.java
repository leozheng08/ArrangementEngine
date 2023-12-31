package cn.tongdun.kunpeng.api.basedata.step.mobile;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.basedata.step.mobile.ext.IMobilePhoneInvokeExtPt;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 手机画像信息获取
 *
 * @Author: liang.chen
 * @Date: 2020/2/10 下午2:19
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.BASIC_DATA)
public class MobilePhoneStep implements IRiskStep {

    @Autowired
    private ExtensionExecutor extensionExecutor;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {

        //调用手机画像之前，调用参数的组织

        //调用手机画像

        //手机画像信息处理

        extensionExecutor.execute(IMobilePhoneInvokeExtPt.class, context.getBizScenario(), mobilePhoneInvokeExtPt -> mobilePhoneInvokeExtPt.fetchData(context, response, request));

        return true;
    }

}
