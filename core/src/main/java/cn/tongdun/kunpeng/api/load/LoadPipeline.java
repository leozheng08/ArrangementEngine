package cn.tongdun.kunpeng.api.load;

import cn.tongdun.tdframework.core.pipeline.Phase;
import cn.tongdun.tdframework.core.pipeline.Pipeline;

/**
 * 启动加载处理流程
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:43
 */
@Pipeline(name = LoadPipeline.NAME )
public class LoadPipeline {

    public final static String NAME = "load";

    //加载字段、字典表等基本信息到内存
    @Phase(parallel = true, order = 100)
    public final static String LOAD_COMM = "loadComm";

    //合作方信息到内存
    @Phase(parallel = true, order = 200)
    public final static String LOAD_PARTNER = "loadPartner";

    //加载策略集到内存
    @Phase(parallel = true, order = 300)
    public final static String LOAD_POLICY = "loadPolicy";

    //预热处理
    @Phase(parallel = true, order = 400)
    public final static String WARN = "warn";

    //打开OK页面
    @Phase(parallel = true, order = 500)
    public final static String OK = "ok";

}

