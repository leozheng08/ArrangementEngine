package cn.tongdun.kunpeng.api.infrastructure.kafka;

import cn.tongdun.kunpeng.api.engine.reload.RawDomainEventHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class DomainEventConsumer extends AbstractConsumer {

    private static final Logger                     logger                   = LoggerFactory.getLogger(DomainEventConsumer.class);

    @Autowired
    private RawDomainEventHandle rawDomainEventHandle;

    @Override
    protected boolean batchConsume() {
        return false;
    }

    @Override
    public void onMessage(String topic, Map message) {
        rawDomainEventHandle.handleRawMessage(message);
    }


}
