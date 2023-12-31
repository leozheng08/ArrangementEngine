package cn.tongdun.kunpeng.api.application.groovy.step;


import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.engine.model.script.DynamicScriptManager;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Step(pipeline = Risk.NAME,phase = Risk.GROOVY)
public class GroovyExecuteStep implements IRiskStep {

    @Autowired
    private DynamicScriptManager dynamicScriptManager;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request){
        dynamicScriptManager.execute(context, response, request);
        return true;
    }
}