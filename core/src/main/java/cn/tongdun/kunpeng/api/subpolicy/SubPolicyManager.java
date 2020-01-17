package cn.tongdun.kunpeng.api.subpolicy;

import cn.tongdun.kunpeng.api.context.FraudContext;
import cn.tongdun.kunpeng.api.rule.Rule;
import cn.tongdun.kunpeng.api.rule.RuleCache;
import cn.tongdun.kunpeng.api.rule.RuleManager;
import cn.tongdun.kunpeng.api.runtime.*;
import cn.tongdun.kunpeng.common.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 子策略执行，根据subPolicyUuid从缓存中取得子策略实体SubPolicy对象后运行。
 * @Author: liang.chen
 * @Date: 2019/12/16 下午7:58
 */
@Component
public class SubPolicyManager implements IExecutor<String,SubPolicyResponse>{

    @Autowired
    SubPolicyCache subPolicyCache;


    @Autowired
    RuleCache ruleCache;

    @Autowired
    RuleManager ruleManager;

    @Override
    public SubPolicyResponse execute(String uuid, FraudContext context){
        SubPolicyResponse subPolicyResponse = new SubPolicyResponse();

        SubPolicy subPolicy = subPolicyCache.get(uuid);
        if(subPolicy == null){
            context.addSubReasonCode(new SubReasonCode(ReasonCode.SUB_POLICY_NOT_FIND.getCode(), ReasonCode.SUB_POLICY_NOT_FIND.getDescription(), "决策引擎执行"));
        }
        long start = System.currentTimeMillis();
        if(subPolicy.getPolicyMode() == null) {
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
        subPolicyResponse.setSubPolicyUuid(subPolicy.getSubPolicyUuid());
        subPolicyResponse.setSubPolicyName(subPolicy.getSubPolicyName());
        subPolicyResponse.setPolicyMode(subPolicy.getPolicyMode());
        subPolicyResponse.setRiskType(subPolicy.getRiskType());
        subPolicyResponse.setCostTime(System.currentTimeMillis()-start);

        return subPolicyResponse;
    }

    /**
     * 首次匹配
     * @param subPolicy
     * @param context
     * @param subPolicyResponse
     */
    private void firstMatch(SubPolicy subPolicy,FraudContext context,SubPolicyResponse subPolicyResponse){
        executePorcess(subPolicy,context,subPolicyResponse,ruleResponse->{
            subPolicyResponse.setScore(ruleResponse.getScore());
            subPolicyResponse.setDecision(ruleResponse.getDecision());
            //return true表示中断规则运行。
            return true;
        });
    }


    /**
     * 最坏匹配
     * @param subPolicy
     * @param context
     * @param subPolicyResponse
     * @return
     */
    private void worstMatch(SubPolicy subPolicy,FraudContext context,SubPolicyResponse subPolicyResponse){
        executePorcess(subPolicy,context,subPolicyResponse,ruleResponse->{
            return false;
        });

        //取得最坏决策结果
        List<HitRule> hitRuleList = subPolicyResponse.getHitRules();
        DecisionResultType decisionResult = subPolicy.getDefaultDecisionResultType();
        for(HitRule hitRule : hitRuleList){
            DecisionResultType newdecisionResult = subPolicy.getDecisionResultType(hitRule.getDecision());
            if(newdecisionResult != null){
                if(newdecisionResult.compareTo(decisionResult)>0){
                    decisionResult = newdecisionResult;
                }
            }
        }
        subPolicyResponse.setDecision(decisionResult.getCode());
    }

    /**
     * 权重匹配
     * @param subPolicy
     * @param context
     * @param subPolicyResponse
     * @return
     */
    private void weighted(SubPolicy subPolicy,FraudContext context,SubPolicyResponse subPolicyResponse){
        executePorcess(subPolicy,context,subPolicyResponse,ruleResponse->{
            return false;
        });

        //取得权重分数之和
        List<HitRule> hitRuleList = subPolicyResponse.getHitRules();
        int score = 0;
        for(HitRule hitRule : hitRuleList){
            score += hitRule.getScore();
        }

        Collection<DecisionResultType> decisionResultTypeList = subPolicy.getDecisionResultMap().values();
        DecisionResultType decisionResult = subPolicy.getDefaultDecisionResultType();
        for(DecisionResultType newDecisionResult:decisionResultTypeList){
            if(score >= newDecisionResult.getStartThreshold() && score < newDecisionResult.getEndThreshold()){
                decisionResult = newDecisionResult;
            }
        }
        subPolicyResponse.setDecision(decisionResult.getCode());
        subPolicyResponse.setScore(score);
    }


    //执行流程控制
    private void executePorcess(SubPolicy subPolicy, FraudContext context, SubPolicyResponse subPolicyResponse, Function<RuleResponse,Boolean> func){
        Map<String,Boolean> hitMap = new HashMap<>();
        for(String ruleUuid:subPolicy.getRuleUuidList()){
            //子规则在上级规则命中情况下才能运行，
            Rule rule = ruleCache.get(ruleUuid);
            if(rule.getParentUuid() != null){
                Boolean parentHit = hitMap.get(rule.getParentUuid());
                if(parentHit == null || !parentHit){
                    break;
                }
            }

            //执行此规则
            RuleResponse ruleResponse = ruleManager.execute(ruleUuid,context);

            //命中中断规则，则中断退出，不再运行后继规则
            if(ruleResponse.isTerminate()){
                break;
            }

            if(ruleResponse.isHit()){
                hitMap.put(ruleUuid,true);
                HitRule hitRule = new HitRule();
                hitRule.setId(ruleResponse.getId());
                hitRule.setName(ruleResponse.getName());
                hitRule.setUuid(ruleResponse.getUuid());
                hitRule.setParentUuid(ruleResponse.getParentUuid());
                hitRule.setScore(ruleResponse.getScore());
                hitRule.setDecision(ruleResponse.getDecision());
                subPolicyResponse.addHitRule(hitRule);

                //如果返回true则退出
                if(func.apply(ruleResponse)){
                    break;
                }
            }
        }
    }
}
