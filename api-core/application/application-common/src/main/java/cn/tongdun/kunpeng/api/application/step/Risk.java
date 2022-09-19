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

    @Phase(parallel = true, timeOut = 210, poolSize = 100, order = 2000)
    public final static String BASIC_DATA = "baseData";

    /**
     * 阿拉丁黑白名单 此phase会根据入参判断是否调用黑白名单，入参依赖于basicData的结果，所以必须在其之后
     * 同时 如果命中了直接跳到output阶段，不执行GROOVY、RULE_DATA、RUN_ENGINE
     * 同时 命中了阿拉丁名单也要流量上报 但BILLING_INCREASE不能声明为required = true，需要在ALADDIN_BLACK_WHITE_LIST内部调用流量上报的service
     */
    @Phase(parallel = false, order = 2100)
    public final static String ALADDIN_BLACK_WHITE_LIST = "aladdinBlackWhiteList";

    @Phase(parallel = true, timeOut = 200, poolSize = 100, order = 3000)
    public final static String GROOVY = "groovy";

    @Phase(parallel = true, timeOut = 800, poolSize = 100, order = 4000)
    public final static String RULE_DATA = "ruleData";

    /**
     * 策略指标不能事先计算好，因为策略指标可能会依赖三方/模型的结果，下沉到context.getPolicyIndex获取
    @Phase(parallel = false, order = 5000)
    public final static String POLICY_INDEX = "policyIndex";
     **/

    @Phase(parallel = false, order = 6000)
    public final static String RUN_ENGINE = "runEngine";

    @Phase(parallel = false, order = 6500)
    public final static String BILLING_INCREASE = "billingIncrease";

    // required = true 如果前面各个阶段中断运行后，output仍需要运行
    @Phase(parallel = false, order = 7000, required = true)
    public final static String OUTPUT = "output";

}
