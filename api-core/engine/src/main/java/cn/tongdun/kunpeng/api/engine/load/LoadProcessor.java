package cn.tongdun.kunpeng.api.engine.load;

import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.exception.SysException;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import cn.tongdun.tdframework.core.pipeline.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 启动时缓存加载处理器
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:43
 */
@Component
public class LoadProcessor implements ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = LoggerFactory.getLogger(PipelineExecutor.class);


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info(TraceUtils.getFormatTrace()+"启动初始化 start");
        PipelineExecutor pipelineExecutor = null;
        try {
            ApplicationContext context = event.getApplicationContext();
            if (context.getParent() == null) {
                pipelineExecutor = context.getBean(PipelineExecutor.class);
            }
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace()+"启动初始化 error", e);
            throw  e;
        }

        if (null == pipelineExecutor) {
            logger.error(TraceUtils.getFormatTrace()+"pipelineExecutor not find!");
            throw new SysException("pipelineExecutor not find!");
        }

        load(pipelineExecutor);
        logger.info(TraceUtils.getFormatTrace()+"启动初始化 finished");
    }


    public void load(PipelineExecutor pipelineExecutor){

        //当加载不成功或失败时停止加载
        Response result = pipelineExecutor.execute(LoadPipeline.NAME, ILoad.class, step -> step.load(),(isLoad, e)->{
            return e !=null|| (isLoad!=null && !isLoad);
        });

        if(!result.isSuccess()){
            logger.info(TraceUtils.getFormatTrace()+"LoadManager load result:"+result);
            throw new SysException("启动加载失败");
        }


        //根据单个合作方加载数据，暂放在这调用，供测试。后面去除
//        Response result2 = pipelineExecutor.execute(LoadByPartnerPipeline.NAME, ILoadByPartner.class, step -> step.loadByPartner("demo"),(isLoad, e)->{
//            return e !=null|| (isLoad!=null && !isLoad);
//        });
//
//        if(!result2.isSuccess()){
//            throw new SysException("按合作方加载失败");
//        }
//        logger.info(TraceUtils.getFormatTrace()+"LoadByPartnerManager load result:"+result);
    }

}
