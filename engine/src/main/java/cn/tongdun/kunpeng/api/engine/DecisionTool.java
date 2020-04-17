package cn.tongdun.kunpeng.api.engine;

import cn.tongdun.kunpeng.api.engine.model.decisionmode.AbstractDecisionMode;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionModeCache;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultType;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.api.common.data.PolicyResponse;
import cn.tongdun.kunpeng.api.common.data.SubPolicyResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/17 下午7:53
 */
public abstract class DecisionTool implements IExecutor<AbstractDecisionMode, PolicyResponse> {

    @Autowired
    protected DecisionModeCache decisionModeCache;

    @Autowired
    protected DecisionResultTypeCache decisionResultTypeCache;


    protected SubPolicyResponse createFinalSubPolicyResult(List<SubPolicyResponse> subPolicyResponseList) {
        if(subPolicyResponseList == null || subPolicyResponseList.isEmpty()){
            return null;
        }

        //决策结果按高风险排前, 如果风险一样，则按子策略的分数倒排
        Collections.sort(subPolicyResponseList, new Comparator<SubPolicyResponse>() {
            @Override
            public int compare(SubPolicyResponse action1, SubPolicyResponse action2) {

                DecisionResultType decisionResultTyp1 = decisionResultTypeCache.get(action1.getDecision());
                DecisionResultType decisionResultTyp2 = decisionResultTypeCache.get(action2.getDecision());

                int compareValue = 0;
                if(decisionResultTyp1 != null && decisionResultTyp2 != null){
                    compareValue = decisionResultTyp2.compareTo(decisionResultTyp1);
                    if(compareValue != 0){
                        return compareValue;
                    }
                }

                return compareValue = action2.getScore() - action1.getScore();
            }
        });

        return subPolicyResponseList.get(0);
    }
}
