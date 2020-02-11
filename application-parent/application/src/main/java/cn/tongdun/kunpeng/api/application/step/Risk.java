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

    @Phase(parallel = true,order = 100)
    public final static String CHECK = "check";


    @Phase(parallel = true, timeOut = 200,poolSize = 100,order=200)
    public final static String BASIC_DATA = "baseData";

    @Phase(parallel = true, timeOut = 200,poolSize = 100,order=300)
    public final static String GROOVY = "groovy";

    @Phase(parallel = true,timeOut = 800,poolSize = 100,order = 400)
    public final static String RULE_DATA= "ruleData";

    @Phase(parallel = false,order = 500)
    public final static String POLICY_INDEX= "policyIndex";

    @Phase(parallel = false,order = 600)
    public final static String RUN_ENGINE= "runEngine";

    @Phase(parallel = false,order = 700)
    public final static String OUTPUT= "output";

}
