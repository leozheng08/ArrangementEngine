package cn.tongdun.kunpeng.api.engine.model.subpolicy.mode;

import cn.tongdun.kunpeng.api.common.data.*;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultThreshold;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultType;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.client.data.PolicyMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 子策略执行，根据subPolicyUuid从缓存中取得子策略实体SubPolicy对象后运行。
 *
 * @Author: liang.chen
 * @Date: 2019/12/16 下午7:58
 */
@Component
public class WeightedPolicyModeExecuter extends AbstractPolicyModeExecuter {

    private static Logger logger = LoggerFactory.getLogger(WeightedPolicyModeExecuter.class);

    @PostConstruct
    public void init(){
        policyModeCache.put(PolicyMode.Weighted,this);
    }

    /**
     * 权重匹配
     *
     * @param subPolicy
     * @param context
     * @param subPolicyResponse
     * @return
     */
    @Override
    public void executeMatch(SubPolicy subPolicy, AbstractFraudContext context, SubPolicyResponse subPolicyResponse) {
        executePorcess(subPolicy, context, subPolicyResponse, ruleResponse -> {
            //命中规则后的处理逻辑,false表示不中断继续执行后继规则
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
}
