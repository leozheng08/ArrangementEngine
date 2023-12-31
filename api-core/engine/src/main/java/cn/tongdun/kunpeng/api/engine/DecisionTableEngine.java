package cn.tongdun.kunpeng.api.engine;

import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.AbstractDecisionMode;
import cn.tongdun.kunpeng.api.common.data.PolicyResponse;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liang.chen
 * @Date: 2019/12/17 下午7:53
 */
@Component
public class DecisionTableEngine extends DecisionTool{

    @Autowired
    PolicyCache policyCache;

    @Override
    public PolicyResponse execute(AbstractDecisionMode decisionMode, AbstractFraudContext context){
        long start = System.currentTimeMillis();
        PolicyResponse rolicyResponse = new PolicyResponse();

        String policyUuid = decisionMode.getPolicyUuid();
        Policy policy = policyCache.get(policyUuid);

        rolicyResponse.setPolicyUuid(policy.getUuid());
        rolicyResponse.setPolicyName(policy.getName());

        rolicyResponse.setCostTime(System.currentTimeMillis()-start);
        return rolicyResponse;
    }
}
