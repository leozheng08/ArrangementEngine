package cn.tongdun.kunpeng.api.runtime.impl;

import cn.tongdun.kunpeng.api.context.FraudContext;
import cn.tongdun.kunpeng.api.runmode.*;
import cn.tongdun.kunpeng.api.runtime.IExecutor;
import cn.tongdun.kunpeng.common.data.PolicyResponse;
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

    public DecisionTool getDecisionTool(AbstractRunMode abstractRunMode){
        if(abstractRunMode instanceof ParallelSubPolicy){
            return parallelEngine;
        } else if(abstractRunMode instanceof DecisionFlow){
            return decisionFlowEngine;
        } else if(abstractRunMode instanceof DecisionTable){
            return decisionTableEngine;
        } else if(abstractRunMode instanceof DecisionTree){
            return decisionTreeEngine;
        }
        return null;
    }

}
