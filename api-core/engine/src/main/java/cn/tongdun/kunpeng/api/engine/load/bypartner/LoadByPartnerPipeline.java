package cn.tongdun.kunpeng.api.engine.load.bypartner;

import cn.tongdun.tdframework.core.pipeline.Phase;
import cn.tongdun.tdframework.core.pipeline.Pipeline;

/**
 * 启动加载处理流程
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:43
 */
@Pipeline(name = LoadByPartnerPipeline.NAME )
public class LoadByPartnerPipeline {

    public final static String NAME = "loadByPartner";

    //合作方信息到内存
    @Phase(parallel = true, order = 200, poolSize = 2, queueSize = 10,timeOut = 0)
    public final static String LOAD_PARTNER = "loadPartner";

    //加载策略集到内存
    @Phase(parallel = true, order = 300, poolSize = 2, queueSize = 10,timeOut = 0)
    public final static String LOAD_POLICY = "loadPolicy";


}

