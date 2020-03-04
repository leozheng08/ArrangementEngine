package cn.tongdun.kunpeng.api.application.activity;

import cn.tongdun.kunpeng.api.application.msg.EventStoreMsgBus;
import cn.tongdun.kunpeng.common.data.QueueItem;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 注册worker，及启动多个线程消费消息
 * @Author: liang.chen
 * @Date: 2020/3/3 下午7:42
 */
public class ActivityStoreKafkaManager {

    private int IN_MEMORY_COUNT = 10000;
    private int THREAD_COUNT = 32;

    @Autowired
    private ActivityStoreKafkaWorker activityKafkaWorker;

    @Autowired
    private EventStoreMsgBus eventStoreMsgBus;


    private List<WorkerThread> workers = new ArrayList<>();


    @PostConstruct
    public void init() {
        addWorker(activityKafkaWorker, THREAD_COUNT);
    }

    private void addWorker(EventWorker worker, int threadCount) {

        BlockingQueue<QueueItem> memoryQueue = new LinkedBlockingDeque<>(IN_MEMORY_COUNT);

        eventStoreMsgBus.addBlockingQueue(worker.getName(), worker.getFilter(),memoryQueue);

        // 启动Worker的线程
        for (int i = 0; i < threadCount; i++) {
            WorkerThread workerThread = startWorkerThread(worker, i, memoryQueue);
            workers.add(workerThread);
        }
    }


    private WorkerThread startWorkerThread(EventWorker worker, int workerId, BlockingQueue<QueueItem> queue) {
        WorkerThread t = new WorkerThread(worker, queue);
        t.setName("Worker-" + worker.getName() + "-" + workerId);
        t.start();
        return t;
    }
}
