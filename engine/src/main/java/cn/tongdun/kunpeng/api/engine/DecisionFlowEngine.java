package cn.tongdun.kunpeng.api.engine;

import cn.fraudmetrix.module.tdflow.model.GraphResult;
import cn.fraudmetrix.module.tdflow.model.NodeResult;
import cn.tongdun.kunpeng.api.engine.constant.DecisionFlowConstant;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionFlow;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.AbstractDecisionMode;
import cn.tongdun.kunpeng.common.data.PolicyResponse;
import cn.tongdun.kunpeng.common.data.SubPolicyResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2019/12/17 下午7:53
 */
@Component
public class DecisionFlowEngine extends DecisionTool {

    private static Logger logger = LoggerFactory.getLogger(DecisionFlowEngine.class);

    @Autowired
    private PolicyCache policyCache;

    @Override
    public PolicyResponse execute(AbstractDecisionMode decisionMode, AbstractFraudContext context) {

        DecisionFlow decisionFlow = (DecisionFlow) decisionMode;

        long start = System.currentTimeMillis();
        PolicyResponse policyResponse = new PolicyResponse();

        String policyUuid = decisionMode.getPolicyUuid();
        Policy policy = policyCache.get(policyUuid);

        GraphResult graphResult = decisionFlow.getGraph().execute(context);
        if (graphResult.getException() != null) {
            logger.error("DecisionFlowEngine execute error!", graphResult.getException());
        }

        Map<String, NodeResult> nodeResultMap = graphResult.getNodeResultMap();
        List<SubPolicyResponse> subPolicyResponseList = new ArrayList<>(5);
        for (Map.Entry<String, NodeResult> entry : nodeResultMap.entrySet()) {
            NodeResult nodeResult = entry.getValue();
            Map<String, Object> resultMap = nodeResult.getResultMap();
            if (resultMap.containsKey(DecisionFlowConstant.SUB_POLICY_NODE_RESULT)) {
                SubPolicyResponse subPolicyResponse = (SubPolicyResponse) resultMap.get(DecisionFlowConstant.SUB_POLICY_NODE_RESULT);
                subPolicyResponseList.add(subPolicyResponse);
            }
        }


        policyResponse.setSubPolicyResponses(subPolicyResponseList);


        policyResponse.setPolicyUuid(policy.getUuid());
        policyResponse.setPolicyName(policy.getName());
        //取最坏策略结果
        SubPolicyResponse finalSubPolicyResponse = createFinalSubPolicyResult(subPolicyResponseList);
        policyResponse.setDecision(finalSubPolicyResponse.getDecision());
        policyResponse.setScore(finalSubPolicyResponse.getScore());

        policyResponse.setCostTime(System.currentTimeMillis() - start);
        return policyResponse;
    }
}
