package cn.tongdun.kunpeng.api.engine;

import cn.fraudmetrix.module.tdflow.model.GraphResult;
import cn.fraudmetrix.module.tdflow.model.NodeResult;
import cn.tongdun.kunpeng.api.engine.constant.DecisionFlowConstant;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionFlow;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.AbstractDecisionMode;
import cn.tongdun.kunpeng.api.common.data.PolicyResponse;
import cn.tongdun.kunpeng.api.common.data.SubPolicyResponse;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: liang.chen
 * @Date: 2019/12/17 下午7:53
 */
@Component
public class DecisionFlowEngine extends DecisionTool {

    private static Logger logger = LoggerFactory.getLogger(DecisionFlowEngine.class);

    @Autowired
    private PolicyCache policyCache;

    @Autowired
    private DecisionResultTypeCache decisionResultTypeCache;

    @Override
    public PolicyResponse execute(AbstractDecisionMode decisionMode, AbstractFraudContext context) {

        DecisionFlow decisionFlow = (DecisionFlow) decisionMode;

        long start = System.currentTimeMillis();
        PolicyResponse policyResponse = new PolicyResponse();

        // 试运行的策略结果
        PolicyResponse tryPolicyResponse = new PolicyResponse();

        Policy policy = policyCache.get(context.getPolicyUuid());

        GraphResult graphResult = decisionFlow.getGraph().execute(context);
        if (graphResult.getException() != null) {
            logger.error(TraceUtils.getFormatTrace() + "DecisionFlowEngine execute error!", graphResult.getException());
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



        policyResponse.setPolicyUuid(policy.getUuid());
        policyResponse.setPolicyName(policy.getName());

        // 是否有试运行权限
        if(context.isPilotRun()){
            tryPolicyResponse.setPolicyUuid(policy.getUuid());
            tryPolicyResponse.setPolicyName(policy.getName());
        }

        context.setPolicyVersion(policy.getVersion());

        //填充正式的PolicyResponse
        fillPolicyResponse(policyResponse, subPolicyResponseList, start);

        if (context.isPilotRun()) {
            fillTryPolicyResponse(tryPolicyResponse, subPolicyResponseList, start);
            context.setTryPolicyResponse(tryPolicyResponse);
        }

        return policyResponse;
    }


    // 填充正式的PolicyResponse
    public void fillPolicyResponse(PolicyResponse policyResponse, List<SubPolicyResponse> subPolicyResponseList, long start) {
        policyResponse.setSuccess(true);
        policyResponse.setSubPolicyResponses(subPolicyResponseList);

        // 获取最坏的策略结果
        SubPolicyResponse finalSubPolicyResponse = createFinalSubPolicyResult(subPolicyResponseList);

        if (Objects.nonNull(finalSubPolicyResponse)) {
            policyResponse.setDecision(finalSubPolicyResponse.getDecision());
            policyResponse.setScore(finalSubPolicyResponse.getScore());
        }else {
            policyResponse.setDecision(decisionResultTypeCache.getDefaultType().getCode());
        }

        policyResponse.setRiskType(finalSubPolicyResponse.getRiskType());
        policyResponse.setCostTime(System.currentTimeMillis() - start);
    }

    // 填充试运行的PolicyResponse
    public void fillTryPolicyResponse(PolicyResponse policyResponse, List<SubPolicyResponse> subPolicyResponseList, long start) {
        policyResponse.setSuccess(true);
        policyResponse.setSubPolicyResponses(subPolicyResponseList);

        // 获取最坏的策略结果
        SubPolicyResponse finalSubPolicyResponse = createTryFinalSubPolicyResult(subPolicyResponseList);

        if (Objects.nonNull(finalSubPolicyResponse)) {
            policyResponse.setDecision(finalSubPolicyResponse.getTryDecision());
            policyResponse.setScore(finalSubPolicyResponse.getTryScore());
        }else {
            policyResponse.setDecision(decisionResultTypeCache.getDefaultType().getCode());
        }

        policyResponse.setRiskType(finalSubPolicyResponse.getRiskType());
        policyResponse.setCostTime(System.currentTimeMillis() - start);
    }

}
