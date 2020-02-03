package cn.tongdun.kunpeng.api.load;

import cn.tongdun.tdframework.core.logger.Logger;
import cn.tongdun.tdframework.core.logger.LoggerFactory;
import cn.tongdun.tdframework.core.pipeline.IStep;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午4:30
 */
public interface ILoad extends IStep {


    boolean load();

    @Override
    default void errorHandle(Throwable e) {
        Logger logger = LoggerFactory.getLogger(ILoad.class);
        logger.error(this.getClass().getSimpleName()+ " load error",e);
    };
}
