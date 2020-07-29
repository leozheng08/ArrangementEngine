package cn.tongdun.kunpeng.api.application.activity.ext;

import cn.tongdun.kunpeng.api.application.activity.common.ActivityStoreKafkaWorker;
import cn.tongdun.kunpeng.api.application.step.ext.ISendKafkaExtPt;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.QueueItem;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.Extension;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: yuanhang
 * @date: 2020-07-28 17:59
 **/
@Extension(business = BizScenario.DEFAULT,tenant = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class DefaultSendKafkaExt implements ISendKafkaExtPt {

    @Autowired
    private ActivityStoreKafkaWorker activityStoreKafkaWorker;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        QueueItem queueItem = new QueueItem(context,response,request);
        if(activityStoreKafkaWorker.getFilter().test(queueItem)) {
            activityStoreKafkaWorker.onEvent(queueItem);
        }
        return true;
    }
}
