package cn.tongdun.kunpeng.api.application.policyindex;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndexManager;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.RiskResponse;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: liuq
 * @Date: 2020/2/18 1:48 PM
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.POLICY_INDEX)
public class PolicyIndexEngine implements IRiskStep {

    @Autowired
    private PolicyIndexManager policyIndexManager;


    @Override
    public boolean invoke(AbstractFraudContext context, RiskResponse response, Map<String, String> request) {

        policyIndexManager.execute(context.getPolicyUuid(), context);
        return true;
    }
}
