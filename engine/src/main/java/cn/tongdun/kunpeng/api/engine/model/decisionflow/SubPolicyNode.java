package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import cn.fraudmetrix.module.tdflow.definition.NodeDesc;
import cn.fraudmetrix.module.tdflow.exception.ParseException;
import cn.fraudmetrix.module.tdflow.model.NodeResult;
import cn.fraudmetrix.module.tdflow.model.node.AbstractBizNode;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.tongdun.kunpeng.api.engine.constant.DecisionFlowConstant;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyManager;
import cn.tongdun.kunpeng.client.data.SubPolicyResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: liuq
 * @Date: 2020/2/18 5:52 PM
 */
public class SubPolicyNode extends AbstractBizNode {

    private static Logger logger = LoggerFactory.getLogger(SubPolicyNode.class);

    private String subPolicyUuid;

    @Override
    protected NodeResult run(ExecuteContext executeContext) {
        SubPolicyManager subPolicyManager = (SubPolicyManager) SpringContextHolder.getBean("subPolicyManager");
        SubPolicyResponse subPolicyResponse = subPolicyManager.execute(subPolicyUuid, (AbstractFraudContext) executeContext);

        NodeResult nodeResult = new NodeResult();
        nodeResult.putOneResult(DecisionFlowConstant.SUB_POLICY_NODE_RESULT, subPolicyResponse);
        return nodeResult;
    }

    @Override
    public int getRunCost() {
        return 30;
    }

    @Override
    public void parse(NodeDesc nodeDesc) {
        setId(nodeDesc.getId());
        Map<String, String> paramMap = nodeDesc.getParamList().stream().collect(Collectors.toMap(a -> a.getName(), b -> b.getValue()));
        subPolicyUuid = paramMap.get("tns:ruleFlowGroup");
        if (StringUtils.isBlank(subPolicyUuid)) {
            throw new ParseException("Policy Node id:" + this.getId() + " have no subPolicyUuid,tns:ruleFlowGroup is blank!");
        }
    }
}
