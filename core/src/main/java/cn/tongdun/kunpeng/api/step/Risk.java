package cn.tongdun.kunpeng.api.step;

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
    public final static String PRE = "PRE";


    @Phase(parallel = true, timeOut = 200,poolSize = 1,order=200)
    public final static String BASIC_DATA = "BASIC_DATA";

    @Phase(parallel = true,order = 300)
    public final static String RULE_DATA= "RULE_DATA";


    @Phase(parallel = false,order = 400)
    public final static String ENGINE= "ENGINE";
}
