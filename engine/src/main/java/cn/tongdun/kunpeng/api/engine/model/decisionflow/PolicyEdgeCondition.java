package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import cn.fraudmetrix.module.tdflow.definition.EdgeDesc;
import cn.fraudmetrix.module.tdflow.exception.ParseException;
import cn.fraudmetrix.module.tdflow.model.edge.EdgeCondition;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyManager;
import cn.tongdun.kunpeng.common.data.SubPolicyResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: liuq
 * @Date: 2020/2/19 2:44 PM
 */
public class PolicyEdgeCondition extends EdgeCondition {

    private String subPolicyUuid;

    @Override
    public void parse(EdgeDesc edgeDesc) {
        Map<String, String> paramMap = edgeDesc.getParamList().stream().collect(Collectors.toMap(a -> a.getName(), b -> b.getValue()));
        subPolicyUuid = paramMap.get("tns:taskName");
        if (StringUtils.isBlank(subPolicyUuid)) {
            throw new ParseException("Edge id:" + edgeDesc.getId() + " subPolicy edge tns:taskName is blank!");
        }
    }

    @Override
    public Boolean eval(ExecuteContext executeContext) {
        SubPolicyManager subPolicyManager = (SubPolicyManager) SpringContextHolder.getBean("subPolicyManager");
        SubPolicyResponse subPolicyResponse = subPolicyManager.execute(subPolicyUuid, (AbstractFraudContext) executeContext);

        return subPolicyResponse.getHitRules() != null && !subPolicyResponse.getHitRules().isEmpty();
    }
}
