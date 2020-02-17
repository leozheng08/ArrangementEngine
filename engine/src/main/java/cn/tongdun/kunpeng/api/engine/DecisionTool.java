package cn.tongdun.kunpeng.api.engine;

import cn.tongdun.kunpeng.api.engine.model.decisionmode.AbstractDecisionMode;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionModeCache;
import cn.tongdun.kunpeng.common.data.PolicyResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: liang.chen
 * @Date: 2019/12/17 下午7:53
 */
public abstract class DecisionTool implements IExecutor<AbstractDecisionMode,PolicyResponse>{

    @Autowired
    protected DecisionModeCache decisionModeCache;


}
