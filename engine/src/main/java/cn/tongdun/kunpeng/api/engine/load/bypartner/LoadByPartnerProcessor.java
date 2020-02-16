package cn.tongdun.kunpeng.api.engine.load.bypartner;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.tdframework.common.dto.Response;
import cn.tongdun.tdframework.core.exception.SysException;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class LoadByPartnerProcessor {

    private Logger logger = LoggerFactory.getLogger(PipelineExecutor.class);

    @Autowired
    private PipelineExecutor pipelineExecutor;

    public void loadByPartner(String partnerCode){
        logger.info("LoadManager loadByPartner:"+partnerCode);

        Response result = pipelineExecutor.execute(LoadPipeline.NAME,
                ILoadByPartner.class, step -> step.loadByPartner("demo"),
                (isLoad, e)->{
                    return e !=null|| (isLoad!=null && !isLoad);
                }
        );
        logger.info("LoadManager load result:"+result);

        if(!result.isSuccess()){
            logger.error("按合作方加载失败"+partnerCode);
        }
    }

}
