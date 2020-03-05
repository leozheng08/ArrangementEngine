package cn.tongdun.kunpeng.api.application.activity.ext;

import cn.tongdun.kunpeng.api.application.activity.ActitivyMsg;
import cn.tongdun.kunpeng.api.application.activity.IActitivyMsg;
import cn.tongdun.kunpeng.api.application.activity.IGenerateActivityExtPt;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.BizScenario;
import cn.tongdun.kunpeng.common.data.QueueItem;
import cn.tongdun.tdframework.core.extension.Extension;

/**
 * @Author: liang.chen
 * @Date: 2020/3/4 下午4:00
 */
@Extension(business = BizScenario.DEFAULT,tenant = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class GenerateActivityExt implements IGenerateActivityExtPt{

    /**
     * 根据出入参、上下文生成Activity消息
     * @param queueItem
     * @return
     */
    @Override
    public IActitivyMsg generateActivity(QueueItem queueItem){
        AbstractFraudContext context = queueItem.getContext();

        ActitivyMsg actitivy = new ActitivyMsg();
        actitivy.setProduceTime(System.currentTimeMillis());
        actitivy.setSeqId(context.getSeqId());


        actitivy.setResponse(queueItem.getResponse());
        actitivy.setSubReasonCodes(context.getSubReasonCodes());
        return null;
    }
}
