package cn.tongdun.kunpeng.api.application.activity;

import cn.tongdun.kunpeng.api.application.msg.EventStoreMsgBus;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.QueueItem;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 同步保存activity消息到kafka,为决策接口所有步骤中的最后一个步骤
 * @Author: liang.chen
 * @Date: 2020/2/20 下午5:54
 */
@Component
@Step(pipeline = Risk.NAME,phase = Risk.OUTPUT,order = 3000)
public class SyncEventStoreStep implements IRiskStep {

    @Autowired
    private ActivityStoreKafkaWorker activityStoreKafkaWorker;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request){

        QueueItem queueItem = new QueueItem(context,response,request);
        if(activityStoreKafkaWorker.getFilter().test(queueItem)) {
            activityStoreKafkaWorker.onEvent(queueItem);
        }
        return true;
    }

}
