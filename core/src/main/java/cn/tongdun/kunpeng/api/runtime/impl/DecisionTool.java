package cn.tongdun.kunpeng.api.runtime.impl;

import cn.tongdun.kunpeng.api.context.FraudContext;
import cn.tongdun.kunpeng.api.policy.Policy;
import cn.tongdun.kunpeng.api.runmode.AbstractRunMode;
import cn.tongdun.kunpeng.api.runmode.RunModeCache;
import cn.tongdun.kunpeng.api.runtime.IExecutor;
import cn.tongdun.kunpeng.common.data.PolicyResponse;
import cn.tongdun.kunpeng.common.data.Response;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: liang.chen
 * @Date: 2019/12/17 下午7:53
 */
public abstract class DecisionTool implements IExecutor<AbstractRunMode,PolicyResponse>{

    @Autowired
    protected RunModeCache runModeCache;


}
