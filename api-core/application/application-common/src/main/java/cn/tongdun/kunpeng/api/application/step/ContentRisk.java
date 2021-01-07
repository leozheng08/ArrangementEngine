package cn.tongdun.kunpeng.api.application.step;

import cn.tongdun.tdframework.core.pipeline.Phase;
import cn.tongdun.tdframework.core.pipeline.Pipeline;
import cn.tongdun.tdframework.core.pipeline.ReferencePhase;

/**
 * @Author: liang.chen
 * @Date: 2019/11/27 下午8:06
 */
@Pipeline(name = ContentRisk.NAME)
public class ContentRisk {

    public final static String NAME = "contentRisk";

//    @Phase(parallel = false, order = 1000)
    @ReferencePhase(originPipeline = Risk.NAME, originPhase = Risk.CHECK, order = 1000)
    public final static String CHECK = "check";

//    @Phase(parallel = true, timeOut = 200, poolSize = 100, order = 2000)
//    public final static String BASIC_DATA = "baseData";

//    @Phase(parallel = true, timeOut = 200, poolSize = 100, order = 3000)
//    public final static String GROOVY = "groovy";

//    @Phase(parallel = true, timeOut = 800, poolSize = 100, order = 4000)
//    public final static String RULE_DATA = "ruleData";

//    @Phase(parallel = false, order = 5000)
    @ReferencePhase(originPipeline = Risk.NAME, originPhase = Risk.POLICY_INDEX, order = 5000)
    public final static String POLICY_INDEX = "policyIndex";

//    @Phase(parallel = false, order = 6000)
    @ReferencePhase(originPipeline = Risk.NAME, originPhase = Risk.RUN_ENGINE, order = 6000)
    public final static String RUN_ENGINE = "runEngine";


    // required = true 如果前面各个阶段中断运行后，output仍需要运行
//    @Phase(parallel = false, order = 7000, required = true)
    @ReferencePhase(originPipeline = Risk.NAME, originPhase = Risk.OUTPUT, order = 7000)
    public final static String OUTPUT = "output";

}
