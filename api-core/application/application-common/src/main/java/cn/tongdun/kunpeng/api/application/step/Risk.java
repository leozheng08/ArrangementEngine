package cn.tongdun.kunpeng.api.application.step;

import cn.tongdun.tdframework.core.pipeline.Phase;
import cn.tongdun.tdframework.core.pipeline.Pipeline;

/**
 * @Author: liang.chen
 * @Date: 2019/11/27 下午8:06
 */
@Pipeline(name = Risk.NAME)
public class Risk {

    public final static String NAME = "risk";

    @Phase(parallel = false, order = 1000)
    public final static String CHECK = "check";

    @Phase(parallel = true, timeOut = 200, poolSize = 100, order = 2000)
    public final static String BASIC_DATA = "baseData";

    @Phase(parallel = true, timeOut = 200, poolSize = 100, order = 3000)
    public final static String GROOVY = "groovy";

    @Phase(parallel = true, timeOut = 800, poolSize = 100, order = 4000)
    public final static String RULE_DATA = "ruleData";

    @Phase(parallel = false, order = 5000)
    public final static String POLICY_INDEX = "policyIndex";

    @Phase(parallel = false, order = 6000)
    public final static String RUN_ENGINE = "runEngine";

    @Phase(parallel = false, order = 6500)
    public final static String BILLING_INCREASE = "billingIncrease";

    // required = true 如果前面各个阶段中断运行后，output仍需要运行
    @Phase(parallel = false, order = 7000, required = true)
    public final static String OUTPUT = "output";

}
