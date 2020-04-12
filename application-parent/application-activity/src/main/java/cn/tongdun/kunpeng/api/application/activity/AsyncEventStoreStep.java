package cn.tongdun.kunpeng.api.application.activity;

import cn.tongdun.kunpeng.api.application.msg.EventStoreMsgBus;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.QueueItem;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 异步保存activity消息到kafka,为决策接口所有步骤中的最后一个步骤
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
