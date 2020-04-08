package cn.tongdun.kunpeng.api.application.activity;

import cn.tongdun.kunpeng.api.common.data.QueueItem;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

/**
 * activity发kafka
 * @Author: liang.chen
 * @Date: 2020/3/3 下午7:42
 */
@Component
public class ActivityStoreKafkaWorker implements IEventWorker {

    @Autowired
    private ExtensionExecutor extensionExecutor;

    @Autowired
    private IActivityMsgRepository activityMsgRepository;

    @Override
    public String getName(){
        return getClass().getSimpleName();
    }


    //过滤条件,在入队列之前过滤
    @Override
    public Predicate<QueueItem> getFilter(){
        return (item)->{
            //过滤掉测试标记的
            if(item.getContext().isTestFlag()){
                return false;
            }
            return true;
        };
    }

    @Override
    public void onEvent(QueueItem item){

        if(item.getContext() == null){
            return;
        }

        //生成activity消息
        IActitivyMsg actitivyMsg = generateActivity(item);

        //发送到kafka
        sendToKafka(actitivyMsg);

    }


    private IActitivyMsg generateActivity(QueueItem item){
        IActitivyMsg actitivyMsg = extensionExecutor.execute(IGenerateActivityExtPt.class, item.getContext().getBizScenario(),
                extension -> extension.generateActivity(item));
        return actitivyMsg;
    }

    private void sendToKafka(IActitivyMsg actitivyMsg){
        activityMsgRepository.sendRawActivity(actitivyMsg.getMessageKey(),actitivyMsg.toJsonString());
    }
}
