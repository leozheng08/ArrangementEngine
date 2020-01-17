package cn.tongdun.kunpeng.api.core.engine;

import cn.tongdun.kunpeng.api.core.runmode.AbstractRunMode;
import cn.tongdun.kunpeng.api.core.runmode.RunModeCache;
import cn.tongdun.kunpeng.api.core.engine.IExecutor;
import cn.tongdun.kunpeng.common.data.PolicyResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: liang.chen
 * @Date: 2019/12/17 下午7:53
 */
public abstract class DecisionTool implements IExecutor<AbstractRunMode,PolicyResponse>{

    @Autowired
    protected RunModeCache runModeCache;


}
