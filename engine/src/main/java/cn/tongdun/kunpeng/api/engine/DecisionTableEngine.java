package cn.tongdun.kunpeng.api.engine;

import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.runmode.AbstractRunMode;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.PolicyResponse;
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
    public PolicyResponse execute(AbstractRunMode abstractRunMode, AbstractFraudContext context){
        long start = System.currentTimeMillis();
        PolicyResponse rolicyResponse = new PolicyResponse();

        String policyUuid = abstractRunMode.getPolicyUuid();
        Policy policy = policyCache.get(policyUuid);

        rolicyResponse.setPolicyUuid(policy.getPolicyUuId());
        rolicyResponse.setPolicyName(policy.getName());
        rolicyResponse.setRiskType(policy.getRiskType());

        rolicyResponse.setCostTime(System.currentTimeMillis()-start);
        return rolicyResponse;
    }
}
