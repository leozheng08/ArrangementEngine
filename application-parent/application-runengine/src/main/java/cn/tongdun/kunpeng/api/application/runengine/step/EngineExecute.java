package cn.tongdun.kunpeng.api.application.runengine.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.engine.model.runmode.AbstractRunMode;
import cn.tongdun.kunpeng.api.engine.model.runmode.RunModeCache;
import cn.tongdun.kunpeng.api.engine.DecisionTool;
import cn.tongdun.kunpeng.api.engine.DecisionToolFactory;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.PolicyResponse;
import cn.tongdun.kunpeng.common.data.RiskResponse;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 决策流/规则引擎执行。
 * @Author: liang.chen
 * @Date: 2019/12/17 下午8:58
 */
@Step(pipeline = Risk.NAME,phase = Risk.RUN_ENGINE)
public class EngineExecute implements IRiskStep {

    @Autowired
    RunModeCache runModeCache;

    @Autowired
    DecisionToolFactory decisionToolFactory;


    @Override
    public boolean invoke(AbstractFraudContext context, RiskResponse response, Map<String, String> request){

        AbstractRunMode AbstractRunMode = runModeCache.get(context.getPolicyUuid());
        DecisionTool decisionTool = decisionToolFactory.getDecisionTool(AbstractRunMode);
        PolicyResponse policyResponse = decisionTool.execute(AbstractRunMode,context);

        return true;
    }

}
