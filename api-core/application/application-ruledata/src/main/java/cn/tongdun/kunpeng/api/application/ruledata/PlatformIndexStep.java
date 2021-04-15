package cn.tongdun.kunpeng.api.application.ruledata;

import cn.tongdun.kunpeng.api.application.intf.IndicatrixServiceExtPt;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Step(pipeline = Risk.NAME,phase = Risk.RULE_DATA,order = 1100)
public class PlatformIndexStep implements IRiskStep {

    @Autowired
    private ExtensionExecutor extensionExecutor;


    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {

        boolean result = extensionExecutor.execute(IndicatrixServiceExtPt.class, context.getBizScenario(), extension -> extension.calculate(context));
        return true;
    }

}
