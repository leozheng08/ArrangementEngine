package cn.tongdun.kunpeng.api.engine.load;

import cn.tongdun.tdframework.common.dto.Response;
import cn.tongdun.tdframework.core.exception.SysException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 启动加载处理器
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:43
 */
@Component
public class LoadProcessor implements ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = LoggerFactory.getLogger(PipelineExecutor.class);


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("启动初始化 start");
        PipelineExecutor pipelineExecutor = null;
        try {
            ApplicationContext context = event.getApplicationContext();
            if (context.getParent() == null) {
                pipelineExecutor = context.getBean(PipelineExecutor.class);
            }
        } catch (Exception e) {
            logger.error("启动初始化 error", e);
            throw  e;
        }

        if (null == pipelineExecutor) {
            logger.error("pipelineExecutor not find!");
            throw new SysException("pipelineExecutor not find!");
        }

        load(pipelineExecutor);
        logger.info("启动初始化 finished");
    }


    public void load(PipelineExecutor pipelineExecutor){
        //当加载不成功或失败时停止加载
        Response result = pipelineExecutor.execute(LoadPipeline.NAME, ILoad.class, step -> step.load(),(isLoad,e)->{
            return e !=null|| (isLoad!=null && !isLoad);
        });

        if(!result.isSuccess()){
            throw new SysException("启动加载失败");
        }
        logger.info("LoadManager load"+result);
    }

}
