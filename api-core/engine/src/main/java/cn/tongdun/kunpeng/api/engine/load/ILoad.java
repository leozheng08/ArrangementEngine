package cn.tongdun.kunpeng.api.engine.load;

import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.tongdun.tdframework.core.pipeline.IStep;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午4:30
 */
public interface ILoad extends IStep {


    boolean load();

    @Override
    default void errorHandle(Throwable e) {
        Logger logger = LoggerFactory.getLogger(ILoad.class);
        logger.error(TraceUtils.getFormatTrace()+this.getClass().getSimpleName()+ " load error",e);
    };
}
