package cn.tongdun.kunpeng.api.application.activity.direct;

import cn.tongdun.kunpeng.api.application.activity.common.ActivityStoreKafkaWorker;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.application.step.ext.ISendKafkaExtPt;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.QueueItem;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
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
    private ExtensionExecutor extensionExecutor;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request){

        extensionExecutor.execute(ISendKafkaExtPt.class, context.getBizScenario(),
                extension -> extension.invoke(context, response, request));
        return true;
    }

}
