package cn.tongdun.kunpeng.api.engine.model.subpolicy;

import cn.tongdun.kunpeng.api.engine.IExecutor;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultThreshold;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultType;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleManager;
import cn.tongdun.kunpeng.common.data.SubPolicyResponse;
import cn.tongdun.kunpeng.common.data.*;
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
    SubPolicyCache subPolicyCache;

    @Autowired
    DecisionResultTypeCache decisionResultTypeCache;

    @Autowired
    RuleCache ruleCache;

    @Autowired
    RuleManager ruleManager;

    @Override
    public SubPolicyResponse execute(String uuid, AbstractFraudContext context) {
        SubPolicyResponse subPolicyResponse = new SubPolicyResponse();

        SubPolicy subPolicy = subPolicyCache.get(uuid);
        if (subPolicy == null) {
            context.addSubReasonCode(new SubReasonCode(ReasonCode.SUB_POLICY_LOAD_ERROR.getCode(), ReasonCode.SUB_POLICY_LOAD_ERROR.getDescription(), "决策引擎执行"));
            return subPolicyResponse;
        }
        long start = System.currentTimeMillis();
        try {
            if (subPolicy.getPolicyMode() != null) {
                switch (subPolicy.getPolicyMode()) {
                    case FirstMatch: //首次匹配
                        firstMatch(subPolicy, context, subPolicyResponse);
                        break;
                    case WorstMatch:  //最坏匹配
                        worstMatch(subPolicy, context, subPolicyResponse);
                        break;
                    case Weighted: //权重模式
                        weighted(subPolicy, context, subPolicyResponse);
                        break;
                    default:
                        weighted(subPolicy, context, subPolicyResponse);
                        break;
                }
            } else {
                weighted(subPolicy, context, subPolicyResponse);
            }


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

    /**
     * 首次匹配
     *
     * @param subPolicy
     * @param context
     * @param subPolicyResponse
     */
    private void firstMatch(SubPolicy subPolicy, AbstractFraudContext context, SubPolicyResponse subPolicyResponse) {
        executePorcess(subPolicy, context, subPolicyResponse, ruleResponse -> {
            subPolicyResponse.setScore(ruleResponse.getScore());
            subPolicyResponse.setDecision(ruleResponse.getDecision());
            //return true表示中断规则运行。
            return true;
        });
    }


    /**
     * 最坏匹配
     *
     * @param subPolicy
     * @param context
     * @param subPolicyResponse
     * @return
     */
    private void worstMatch(SubPolicy subPolicy, AbstractFraudContext context, SubPolicyResponse subPolicyResponse) {
        executePorcess(subPolicy, context, subPolicyResponse, ruleResponse -> {
            return false;
        });

        //取得最坏决策结果
        List<RuleResponse> hitRuleList = subPolicyResponse.getHitRules();
        DecisionResultType decisionResult = decisionResultTypeCache.getDefaultType();
        for (RuleResponse hitRule : hitRuleList) {
            //根据DecisionResultType的order顺序，Pass、Review、Reject顺序为1、2、3, 序号越大，为最坏结果
            DecisionResultType newdecisionResult = decisionResultTypeCache.get(hitRule.getDecision());
            if (newdecisionResult != null) {
                if (newdecisionResult.compareTo(decisionResult) > 0) {
                    decisionResult = newdecisionResult;
                }
            }
        }

        subPolicyResponse.setDecision(decisionResult.getCode());
    }

    /**
     * 权重匹配
     *
     * @param subPolicy
     * @param context
     * @param subPolicyResponse
     * @return
     */
    private void weighted(SubPolicy subPolicy, AbstractFraudContext context, SubPolicyResponse subPolicyResponse) {
        executePorcess(subPolicy, context, subPolicyResponse, ruleResponse -> {
            return false;
        });

        //取得权重分数之和
        List<RuleResponse> hitRuleList = subPolicyResponse.getHitRules();
        int score = 0;
        for (RuleResponse hitRule : hitRuleList) {
            score += hitRule.getScore();
        }

        List<DecisionResultThreshold>  decisionResultTypeList = subPolicy.getRiskThresholds();
        DecisionResultType decisionResult = null;
        if(decisionResultTypeList != null && !decisionResultTypeList.isEmpty()) {
            int count = 0;
            int size = decisionResultTypeList.size();
            for (DecisionResultThreshold threshold : decisionResultTypeList) {
                if(count == 0 && score < threshold.getEndThreshold()){
                    decisionResult = threshold.getDecisionResultType();
                    break;
                }
                if(count == size-1 && score > threshold.getStartThreshold()){
                    decisionResult = threshold.getDecisionResultType();
                    break;
                }
                if (score >= threshold.getStartThreshold() && score < threshold.getEndThreshold()) {
                    decisionResult = threshold.getDecisionResultType();
                    break;
                }
                count++;
            }
        }
        if(decisionResult == null){
            decisionResult = decisionResultTypeCache.getDefaultType();
        }

        subPolicyResponse.setDecision(decisionResult.getCode());
        subPolicyResponse.setScore(score);
    }

    /**
     * 执行流程控制
     * @param subPolicy
     * @param context
     * @param subPolicyResponse
     * @param breakWhenHitfunc 在规则命中情况下，如果此函数返回true则退出。用于首次匹配模式下，命中第一个即退出。
     */
    private void executePorcess(SubPolicy subPolicy, AbstractFraudContext context,
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
//                HitRule hitRule = new HitRule();
//                hitRule.setId(ruleResponse.getId());
//                hitRule.setName(ruleResponse.getName());
//                hitRule.setUuid(ruleResponse.getUuid());
//                hitRule.setParentUuid(ruleResponse.getParentUuid());
//                hitRule.setScore(ruleResponse.getScore());
//                //决策结果,如Accept、Review、Reject
//                hitRule.setDecision(ruleResponse.getDecision());
//                subPolicyResponse.addHitRule(hitRule);
                subPolicyResponse.setHit(true);
                //如果返回true则退出。用于首次匹配模式下，命中第一个即退出。
                if (breakWhenHitfunc.apply(ruleResponse)) {
                    break;
                }
            }
        }
    }
}
