package cn.tongdun.kunpeng.api.application.challenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.DelayQueue;

/**
 * @author hls
 * @version 1.0
 * @date 2022/8/9 7:07 下午
 */
@Component
public class DelayQueueCache {

    private int ChallengerTaskNum = 100;

    private static final Logger logger = LoggerFactory.getLogger(DelayQueueCache.class);

    private DelayQueue<ChallengerTask> delayQueue = new DelayQueue<>();

    public void put(ChallengerTask task) {
        try {
            logger.info("当前数据已添加,数据seqId={}", task.getRequestData().getFieldValues().get("originalSeqId"));
            delayQueue.add(task);
        } catch (Exception e) {
            logger.error(" delayQueueCache put 执行异常:{}", e);
        }
    }

    public List<ChallengerTask> getChallengerTaskList() {
        List<ChallengerTask> challengerTasks = new ArrayList<>();
        for (int i = 0; i < ChallengerTaskNum; i++) {
            ChallengerTask task = delayQueue.poll();
            if (task == null) {
                break;
            } else {
                challengerTasks.add(task);
            }
        }
        return challengerTasks;
    }
}
