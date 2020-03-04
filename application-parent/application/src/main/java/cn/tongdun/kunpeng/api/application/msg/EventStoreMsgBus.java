package cn.tongdun.kunpeng.api.application.msg;

import cn.tongdun.kunpeng.common.data.QueueItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * @Author: liang.chen
 * @Date: 2020/3/3 下午7:04
 */
@Component
public class EventStoreMsgBus {

    private Logger logger = LoggerFactory.getLogger(EventStoreMsgBus.class);

    private List<WrappedQueue> queues = new ArrayList<>();

    //添加工作的队列。
    public void addBlockingQueue(String workerName,Predicate<QueueItem> filter,BlockingQueue blockingQueue){
        queues.add(new WrappedQueue(workerName,filter,blockingQueue));
    }


    public void addEvent(QueueItem item) {
        for (WrappedQueue wrappedQueue : queues) {
            try {
                if(wrappedQueue.getFilter() != null){
                    //条件成立的才放到队列中
                    if(!wrappedQueue.getFilter().test(item)){
                        return;
                    }
                }

                boolean success = wrappedQueue.getQueue().add(item);
                if (!success) {
                    logger.error("EventStoreMsgBus Failed to store event, workerName:{}", wrappedQueue.getWorkerName());
                }
            } catch (Exception e){
                logger.error("EventStoreMsgBus error, Failed to store event, workerName:{}", wrappedQueue.getWorkerName(),e);
            }
        }
    }


    class WrappedQueue{
        String workerName;
        Predicate<QueueItem> filter;
        BlockingQueue queue;

        public WrappedQueue(String workerName,Predicate<QueueItem> filter,BlockingQueue blockingQueue){
            this.workerName = workerName;
            this.filter = filter;
            this.queue = queue;
        }

        public String getWorkerName() {
            return workerName;
        }

        public void setWorkerName(String workerName) {
            this.workerName = workerName;
        }

        public Predicate<QueueItem> getFilter() {
            return filter;
        }

        public void setFilter(Predicate<QueueItem> filter) {
            this.filter = filter;
        }

        public BlockingQueue getQueue() {
            return queue;
        }

        public void setQueue(BlockingQueue queue) {
            this.queue = queue;
        }
    }

}