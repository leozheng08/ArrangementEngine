package cn.tongdun.kunpeng.api.application.activity.direct;

import cn.tongdun.kunpeng.api.application.activity.common.ActivityStoreKafkaWorker;
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
 * 在决策接口的处理过程中直接发送activity消息到kafka
 * @Author: liang.chen
 * @Date: 2020/2/20 下午5:54
 */
@Component
@Step(pipeline = Risk.NAME,phase = Risk.OUTPUT,order = 3000)
public class DirectSendKafkaStep implements IRiskStep {

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
