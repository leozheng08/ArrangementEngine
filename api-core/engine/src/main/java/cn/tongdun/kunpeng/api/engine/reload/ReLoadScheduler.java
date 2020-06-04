package cn.tongdun.kunpeng.api.engine.reload;

import cn.tongdun.kunpeng.api.acl.event.notice.IDomainEventRepository;
import cn.tongdun.kunpeng.api.engine.load.step.EventTypeLoadManager;
import cn.tongdun.kunpeng.api.engine.load.step.FieldDefinitionLoadManager;
import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liang.chen
 * @Date: 2019/12/11 下午7:07
 */
@Component
@Step(pipeline = LoadPipeline.NAME,phase = LoadPipeline.OK)
public class ReLoadScheduler implements ILoad {

    private Logger logger = LoggerFactory.getLogger(PipelineExecutor.class);

    @Autowired
    private EventTypeLoadManager eventTypeLoadManager;

    @Autowired
    private FieldDefinitionLoadManager fieldLoadManager;

    @Autowired
    private IDomainEventRepository domainEventRepository;

    @Autowired
    private DomainEventHandle domainEventHandle;
    /**
     * 应用启动成功后，增加定时刷新任务
     */
    @Override
    public boolean load(){

        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        //定时器10秒钟执行一次
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    reLoad();
                } catch (Exception e) {
                    logger.error(TraceUtils.getFormatTrace()+"定时刷新缓存异常",e);
                }
            }
        }, 10, 5, TimeUnit.SECONDS);

        return true;
    }

    /**
     * 定时刷新取得最新的domain事件，并更新本地缓存
     */
    public void reLoad(){
        //拉取得最新两分钟的domain事件
        List<String> eventMsgs = domainEventRepository.pullLastEventMsgs();
        if(eventMsgs == null || eventMsgs.isEmpty()){
            return;
        }
        //logger.info(TraceUtils.getFormatTrace()+"eventMsgs size:"+eventMsgs.size());
        domainEventHandle.handleMessage(eventMsgs);
    }



}
