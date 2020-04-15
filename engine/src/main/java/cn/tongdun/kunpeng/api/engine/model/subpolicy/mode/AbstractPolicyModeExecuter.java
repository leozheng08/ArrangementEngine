package cn.tongdun.kunpeng.api.engine.model.subpolicy.mode;

import cn.tongdun.kunpeng.api.common.data.*;
import cn.tongdun.kunpeng.api.engine.IExecutor;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleManager;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 子策略执行，根据subPolicyUuid从缓存中取得子策略实体SubPolicy对象后运行。
 *
 * @Author: liang.chen
 * @Date: 2019/12/16 下午7:58
 */

public abstract class AbstractPolicyModeExecuter implements IExecutor<String, SubPolicyResponse> {

    private static Logger logger = LoggerFactory.getLogger(AbstractPolicyModeExecuter.class);

    @Autowired
    protected SubPolicyCache subPolicyCache;

    @Autowired
    protected DecisionResultTypeCache decisionResultTypeCache;

    @Autowired
    protected RuleCache ruleCache;

    @Autowired
    protected RuleManager ruleManager;

    @Autowired
    protected PolicyModeExecuterFactory policyModeCache;

    //执行匹配
    abstract void executeMatch(SubPolicy subPolicy, AbstractFraudContext context, SubPolicyResponse subPolicyResponse);

    @Override
    public SubPolicyResponse execute(String uuid, AbstractFraudContext context) {

        SubPolicy subPolicy = subPolicyCache.get(uuid);
        if (subPolicy == null) {
            context.addSubReasonCode(new SubReasonCode(ReasonCode.SUB_POLICY_LOAD_ERROR.getCode(), ReasonCode.SUB_POLICY_LOAD_ERROR.getDescription(), "决策引擎执行"));
            return new SubPolicyResponse();
        }

        SubPolicyResponse subPolicyResponse = generateResponse(subPolicy);

        long start = System.currentTimeMillis();
        try {
            //执行匹配，包含有首次匹配、最坏匹配、权重匹配
            executeMatch(subPolicy,context,subPolicyResponse);

            subPolicyResponse.setPolicyUuid(subPolicy.getPolicyUuid());
            subPolicyResponse.setSubPolicyUuid(subPolicy.getUuid());
            subPolicyResponse.setSubPolicyName(subPolicy.getName());
            subPolicyResponse.setPolicyMode(subPolicy.getPolicyMode());
            subPolicyResponse.setRiskType(subPolicy.getRiskType());
            subPolicyResponse.setSuccess(true);
        } catch (Exception e){
            subPolicyResponse.setSuccess(false);
            context.addSubReasonCode(new SubReasonCode(ReasonCode.RULE_ENGINE_ERROR.getCode(), ReasonCode.RULE_ENGINE_ERROR.getDescription(), "决策引擎执行"));
            logger.error("SubPolicyManager execute uuid:{}, seqId:{} ",uuid,context.getSeqId(), e);
        }
        subPolicyResponse.setCostTime(System.currentTimeMillis() - start);
        return subPolicyResponse;
    }


    protected SubPolicyResponse generateResponse(SubPolicy subPolicy) {
        int ruleCount = 1;
        if (null != subPolicy.getRuleUuidList()) {
            ruleCount = subPolicy.getRuleUuidList().size();
        }
        return new SubPolicyResponse(ruleCount);
    }


    /**
     * 执行流程控制
     * @param subPolicy
     * @param context
     * @param subPolicyResponse
     * @param breakWhenHitfunc 在规则命中情况下，如果此函数返回true则退出。用于首次匹配模式下，命中第一个即退出。
     */
    protected void executePorcess(SubPolicy subPolicy, AbstractFraudContext context,
                                SubPolicyResponse subPolicyResponse, Function<RuleResponse, Boolean> breakWhenHitfunc) {
        Map<String, Boolean> hitMap = new HashMap<>();

        for (String ruleUuid : subPolicy.getRuleUuidList()) {
            //子规则在上级规则命中情况下才能运行，
            Rule rule = ruleCache.get(ruleUuid);
            if (StringUtils.isNotBlank(rule.getParentUuid())&&!StringUtils.equals(rule.getParentUuid(),"0")) {
                Boolean parentHit = hitMap.get(rule.getParentUuid());
                if (parentHit == null || !parentHit) {
                    continue;
                }
            }

            //执行此规则
            RuleResponse ruleResponse = ruleManager.execute(ruleUuid, context);
            subPolicyResponse.addRuleResponse(ruleResponse);

            //命中中断规则，则中断退出，不再运行后继规则
            if (ruleResponse.isTerminate()) {
                break;
            }

            if (ruleResponse.isHit()) {
                hitMap.put(ruleUuid, true);
                subPolicyResponse.setHit(true);
                //如果返回true则退出。用于首次匹配模式下，命中第一个即退出。
                if (breakWhenHitfunc.apply(ruleResponse)) {
                    break;
                }
            }
        }
    }
}
