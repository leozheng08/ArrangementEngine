package cn.tongdun.kunpeng.api.application.activity;

import cn.tongdun.kunpeng.common.data.QueueItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.BlockingQueue;

public class WorkerThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(WorkerThread.class);
    private EventWorker              worker;
    private BlockingQueue<QueueItem> queue;

    public WorkerThread(EventWorker worker, BlockingQueue<QueueItem> queue){
        this.worker = worker;
        this.queue = queue;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void run() {
        while (true) {
            QueueItem item = takeQueueItem();
            if (item == null) {
                continue;
            }
            if (item instanceof ExitSignal) {
                break;
            }
            onEventCaught(item);
        }
    }

    private QueueItem takeQueueItem() {
        try {
            return queue.take();
        } catch (InterruptedException ex) {
            return null;
        }
    }

    private void onEventCaught(QueueItem item) {
        try {
            worker.onEvent(item);
        } catch (Throwable ex) {
            logger.error("onEventCaught", ex);
        }
    }

    public void exit() {
        queue.add(new ExitSignal());
    }

    public BlockingQueue<QueueItem> getQueue() {
        return queue;
    }

    public void waitUntilExit() {
        while (true) {
            try {
                this.join();
                return;
            } catch (InterruptedException ex) {
            }
        }
    }

    private static class ExitSignal extends QueueItem {
    }
}
