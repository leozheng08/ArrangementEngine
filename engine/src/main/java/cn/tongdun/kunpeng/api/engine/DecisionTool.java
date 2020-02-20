package cn.tongdun.kunpeng.api.engine;

import cn.tongdun.kunpeng.api.engine.model.decisionmode.AbstractDecisionMode;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionModeCache;
import cn.tongdun.kunpeng.client.data.PolicyResponse;
import cn.tongdun.kunpeng.client.data.SubPolicyResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/17 下午7:53
 */
public abstract class DecisionTool implements IExecutor<AbstractDecisionMode, PolicyResponse> {

    @Autowired
    protected DecisionModeCache decisionModeCache;


    protected SubPolicyResponse createFinalSubPolicyResult(List<SubPolicyResponse> subPolicyResponseList) {
        SubPolicyResponse finalSubPolicyResponse = null;
        for (SubPolicyResponse subPolicyResponse : subPolicyResponseList) {
            if (finalSubPolicyResponse == null) {
                finalSubPolicyResponse = subPolicyResponse;
                continue;
            }

            if (subPolicyResponse.getDecision().compareTo(finalSubPolicyResponse.getDecision()) > 0) {
                finalSubPolicyResponse = subPolicyResponse;
            }
        }
        return finalSubPolicyResponse;
    }
}
