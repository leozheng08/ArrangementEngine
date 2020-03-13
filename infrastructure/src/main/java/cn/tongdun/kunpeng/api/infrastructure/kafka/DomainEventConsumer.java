package cn.tongdun.kunpeng.api.infrastructure.kafka;

import cn.tongdun.kunpeng.api.engine.reload.DomainEventHandle;
import cn.tongdun.kunpeng.api.engine.reload.RawDomainEventHandle;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DomainEventConsumer extends AbstractConsumer {

    private static final Logger                     logger                   = LoggerFactory.getLogger(DomainEventConsumer.class);

    @Autowired
    private RawDomainEventHandle rawDomainEventHandle;

    @Override
    protected boolean batchConsume() {
        return false;
    }

    @Override
    public void onMessage(String topic, JSONObject message) {
        rawDomainEventHandle.handleRawMessage(message);
    }


}
