package cn.tongdun.kunpeng.api.step;

import cn.tongdun.kunpeng.api.context.FraudContext;
import cn.tongdun.kunpeng.api.core.runmode.AbstractRunMode;
import cn.tongdun.kunpeng.api.core.runmode.RunModeCache;
import cn.tongdun.kunpeng.api.core.engine.DecisionTool;
import cn.tongdun.kunpeng.api.core.engine.DecisionToolFactory;
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
@Step(pipeline = Risk.NAME,phase = Risk.ENGINE)
public class EngineExecute implements RiskStep {

    @Autowired
    RunModeCache runModeCache;

    @Autowired
    DecisionToolFactory decisionToolFactory;


    @Override
    public boolean invoke(FraudContext context, RiskResponse response, Map<String, String> request){

        AbstractRunMode AbstractRunMode = runModeCache.get(context.getPolicyUuid());
        DecisionTool decisionTool = decisionToolFactory.getDecisionTool(AbstractRunMode);
        PolicyResponse policyResponse = decisionTool.execute(AbstractRunMode,context);

        return true;
    }

}
