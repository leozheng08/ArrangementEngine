package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author: yuanhang
 * @date: 2020-06-10 14:31
 * 业务接入相关缓存数据
 **/
//@Component
//@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.LOAD_COMM)
public class AccessBusinessLoadManager implements ILoad {

    private Logger logger = LoggerFactory.getLogger(AccessBusinessLoadManager.class);


    @Override
    public boolean load() {
        logger.info(TraceUtils.getFormatTrace() + " load accessBusiness start");
        long start = System.currentTimeMillis();


        return false;
    }
}
