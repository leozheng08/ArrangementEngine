package cn.tongdun.kunpeng.api.application.challenger;

import cn.hutool.bloomfilter.bitMap.BitMap;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.client.api.IRiskService;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.ThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author hls
 * @version 1.0
 * @date 2022/8/9 7:26 下午
 */
@Component
public class ChallengerTimedTask {
    private static final Logger logger = LoggerFactory.getLogger(ChallengerTimedTask.class);

    @Autowired
    private DelayQueueCache delayQueueCache;

    @Autowired
    private IRiskService riskService;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    /**
     * ruleExecute 线程池
     */
    private ExecutorService executeThreadPool;

    @Autowired
    private ThreadService threadService;

    @PostConstruct
    public void init() {
        this.executeThreadPool = threadService.createThreadPool(
                32,
                128,
                30L,
                TimeUnit.MINUTES,
                10,
                "challengerExecute");

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    // 一次性拉取100条数据
                    List<ChallengerTask> challengerTaskList = delayQueueCache.getChallengerTaskList();
                    logger.info("当前拉取的条数={}", challengerTaskList.size());
                    for (ChallengerTask challengerTask : challengerTaskList) {
                        executeThreadPool.submit(new Callable<Boolean>() {
                            @Override
                            public Boolean call() {
                                try {
                                    riskService.riskService(challengerTask.getRequestData(), Risk.NAME);
                                    return true;
                                } catch (Exception e) {
                                    logger.error("executeThreadPool.submit execute 执行异常:{}", e);
                                    return true;
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    logger.error(TraceUtils.getFormatTrace() + "定时刷新挑战者异常", e);
                }
            }
        }, 10, 10, TimeUnit.SECONDS);
    }
}
