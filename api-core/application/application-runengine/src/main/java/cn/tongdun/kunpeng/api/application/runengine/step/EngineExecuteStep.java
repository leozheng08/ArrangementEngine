package cn.tongdun.kunpeng.api.application.runengine.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.AbstractDecisionMode;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionModeCache;
import cn.tongdun.kunpeng.api.engine.DecisionTool;
import cn.tongdun.kunpeng.api.engine.DecisionToolFactory;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.api.common.data.PolicyResponse;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 决策流/规则引擎执行。
 * @Author: liang.chen
 * @Date: 2019/12/17 下午8:58
 */
@Component
@Step(pipeline = Risk.NAME,phase = Risk.RUN_ENGINE)
public class EngineExecuteStep implements IRiskStep {



    @Autowired
    DecisionModeCache decisionModeCache;

    @Autowired
    DecisionToolFactory decisionToolFactory;


    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request){

        AbstractDecisionMode decisionMode = decisionModeCache.get(context.getPolicyUuid());
        DecisionTool decisionTool = decisionToolFactory.getDecisionTool(decisionMode);
        PolicyResponse policyResponse = decisionTool.execute(decisionMode,context);
        context.setPolicyResponse(policyResponse);
        response.setSuccess(policyResponse.isSuccess());
        return true;
    }

}
