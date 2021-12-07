package cn.tongdun.kunpeng.api.application.check.step;

import cn.fraudmetrix.module.tdrule.function.Function;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndex;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndexCache;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 初始化上下文的策略指标信息
 * @Auther qingran.chen
 * @Date 2021/12/6
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.CHECK, order = 1300)
public class GetPolicyIndexStep implements IRiskStep {

    private static final Logger log = LoggerFactory.getLogger(GetPolicyIndexStep.class);

    @Autowired
    private PolicyIndexCache policyIndexCache;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        Map<String, PolicyIndex> policyIndexMap = policyIndexCache.get(context.getPolicyUuid());
        Map<String, Function> policyIndexFunMap = new HashMap<>(policyIndexMap.size());
        for (String policyIndexUuid : policyIndexMap.keySet()
             ) {
            PolicyIndex policyIndex = policyIndexMap.get(policyIndexUuid);
            if (policyIndex != null && policyIndex.getCalculateFunction() != null){
                policyIndexFunMap.put(policyIndexUuid,policyIndex.getCalculateFunction());
            }else {
                log.error("GetPolicyIndexStep error , policyIndexUuid : {} ",policyIndexUuid);
            }
        }
        context.setPolicyIndexFunMap(policyIndexFunMap);
        return true;
    }
}
