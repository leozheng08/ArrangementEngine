package cn.tongdun.kunpeng.api.application.activity.memoryq;

import cn.tongdun.kunpeng.api.application.msg.EventStoreMsgBus;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.QueueItem;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * activity消息先保存到内存队列，另外启线程将内存对列的数据发送到kafka
 * @Author: liang.chen
 * @Date: 2020/2/20 下午5:54
 */
@Component
@Step(pipeline = Risk.NAME,phase = Risk.OUTPUT,order = 3000)
public class AsyncEventStoreStep implements IRiskStep {

    @Autowired
    private EventStoreMsgBus eventStoreMsgBus;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request){
        //任何事件都发，由消息方决定来过滤
        QueueItem queueItem = new QueueItem(context,response,request);
        eventStoreMsgBus.addEvent(queueItem);
        return true;
    }

}
