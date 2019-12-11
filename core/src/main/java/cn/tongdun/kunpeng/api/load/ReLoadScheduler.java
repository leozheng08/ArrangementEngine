package cn.tongdun.kunpeng.api.load;

import cn.tongdun.tdframework.core.logger.Logger;
import cn.tongdun.tdframework.core.logger.LoggerFactory;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liang.chen
 * @Date: 2019/12/11 下午7:07
 */
public class ReLoadScheduler {

    private Logger logger = LoggerFactory.getLogger(PipelineExecutor.class);

    public void init(){
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        //定时器每5秒钟执行一次
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    reLoad();
                } catch (Exception e) {
                    logger.error("定时刷新缓存异常",e);
                }
            }
        }, 10, 10, TimeUnit.MINUTES);
    }

    public void reLoad(){

    }
}
