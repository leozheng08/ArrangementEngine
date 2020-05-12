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
public class WorstMatchModeExecuter extends AbstractPolicyModeExecuter {

    private static Logger logger = LoggerFactory.getLogger(AbstractPolicyModeExecuter.class);

    @PostConstruct
    public void init(){
        policyModeCache.put(PolicyMode.WorstMatch,this);
    }

    /**
     * 最坏匹配
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

}
