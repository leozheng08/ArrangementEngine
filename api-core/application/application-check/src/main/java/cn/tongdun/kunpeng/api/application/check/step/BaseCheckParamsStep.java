package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.check.step.ext.BaseCheckParamsExtPt;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liang.chen
 * @Date: 2020/2/20 下午3:22
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.CHECK, order = 600)
public class BaseCheckParamsStep implements IRiskStep {

    @Autowired
    private ExtensionExecutor extensionExecutor;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        return extensionExecutor.execute(BaseCheckParamsExtPt.class, context.getBizScenario(), baseCheckParamsExtPt -> baseCheckParamsExtPt.fetchData(context, response, request));
    }
}

