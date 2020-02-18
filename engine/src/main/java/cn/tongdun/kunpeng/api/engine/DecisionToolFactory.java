package cn.tongdun.kunpeng.api.engine;

import cn.tongdun.kunpeng.api.engine.model.decisionmode.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liang.chen
 * @Date: 2019/12/17 下午7:53
 */
@Component
public class DecisionToolFactory  {

    @Autowired
    ParallelEngine parallelEngine;

    @Autowired
    DecisionFlowEngine decisionFlowEngine;

    @Autowired
    DecisionTableEngine decisionTableEngine;

    @Autowired
    DecisionTreeEngine decisionTreeEngine;

    public DecisionTool getDecisionTool(AbstractDecisionMode abstractDecisionMode){
        if(abstractDecisionMode instanceof ParallelSubPolicy){
            return parallelEngine;
        } else if(abstractDecisionMode instanceof DecisionFlow){
            return decisionFlowEngine;
        } else if(abstractDecisionMode instanceof DecisionTable){
            return decisionTableEngine;
        } else if(abstractDecisionMode instanceof DecisionTree){
            return decisionTreeEngine;
        }
        return null;
    }

}
