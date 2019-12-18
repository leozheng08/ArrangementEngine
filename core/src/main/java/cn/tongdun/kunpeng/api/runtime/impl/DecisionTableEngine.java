package cn.tongdun.kunpeng.api.runtime.impl;

import cn.tongdun.kunpeng.api.context.FraudContext;
import cn.tongdun.kunpeng.api.policy.Policy;
import cn.tongdun.kunpeng.api.policy.PolicyCache;
import cn.tongdun.kunpeng.api.runmode.AbstractRunMode;
import cn.tongdun.kunpeng.api.runmode.RunModeCache;
import cn.tongdun.kunpeng.api.runtime.IExecutor;
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
    public PolicyResponse execute(AbstractRunMode abstractRunMode, FraudContext context){
        long start = System.currentTimeMillis();
        PolicyResponse rolicyResponse = new PolicyResponse();

        String policyUuid = abstractRunMode.getPolicyUuid();
        Policy policy = policyCache.getPolicy(policyUuid);

        rolicyResponse.setPolicyUuid(policy.getPolicyUuId());
        rolicyResponse.setPolicyName(policy.getName());
        rolicyResponse.setRiskType(policy.getRiskType());

        rolicyResponse.setCostTime(System.currentTimeMillis()-start);
        return rolicyResponse;
    }
}
