package cn.tongdun.kunpeng.api.engine.model.subpolicy;

import cn.tongdun.kunpeng.api.common.data.*;
import cn.tongdun.kunpeng.api.engine.IExecutor;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultThreshold;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultType;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.mode.AbstractPolicyModeExecuter;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.mode.PolicyModeExecuterFactory;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.mode.WeightedPolicyModeExecuter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 子策略执行，根据subPolicyUuid从缓存中取得子策略实体SubPolicy对象后运行。
 *
 * @Author: liang.chen
 * @Date: 2019/12/16 下午7:58
 */
@Component
public class SubPolicyManager implements IExecutor<String, SubPolicyResponse> {

    private static Logger logger = LoggerFactory.getLogger(SubPolicyManager.class);

    @Autowired
    private SubPolicyCache subPolicyCache;

    @Autowired
    private PolicyModeExecuterFactory policyModeExecuterFactory;


    @Override
    public SubPolicyResponse execute(String uuid, AbstractFraudContext context) {

        SubPolicy subPolicy = subPolicyCache.get(uuid);
        if (subPolicy == null) {
            context.addSubReasonCode(new SubReasonCode(ReasonCode.SUB_POLICY_LOAD_ERROR.getCode(), ReasonCode.SUB_POLICY_LOAD_ERROR.getDescription(), "决策引擎执行"));
            return new SubPolicyResponse(){};
        }

        AbstractPolicyModeExecuter policyModeExecuter = null;
        if(subPolicy.getPolicyMode() != null){
            policyModeExecuter = policyModeExecuterFactory.get(subPolicy.getPolicyMode());
        }
        if(policyModeExecuter == null){
            policyModeExecuter = policyModeExecuterFactory.getDefaultExecuter();
        }

        return policyModeExecuter.execute(uuid,context);

    }
}
