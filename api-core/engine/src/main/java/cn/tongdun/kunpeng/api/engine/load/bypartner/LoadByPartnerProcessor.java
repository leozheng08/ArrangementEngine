package cn.tongdun.kunpeng.api.engine.load.bypartner;

import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import cn.tongdun.tdframework.core.pipeline.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        logger.info(TraceUtils.getFormatTrace()+"LoadManager loadByPartner:"+partnerCode);

        Response result = pipelineExecutor.execute(LoadPipeline.NAME,
                ILoadByPartner.class, step -> step.loadByPartner("demo"),
                (isLoad, e)->{
                    return e !=null|| (isLoad!=null && !isLoad);
                }
        );
        logger.info(TraceUtils.getFormatTrace()+"LoadManager load result:"+result);

        if(!result.isSuccess()){
            logger.error(TraceUtils.getFormatTrace()+"按合作方加载失败"+partnerCode);
        }
    }

}
