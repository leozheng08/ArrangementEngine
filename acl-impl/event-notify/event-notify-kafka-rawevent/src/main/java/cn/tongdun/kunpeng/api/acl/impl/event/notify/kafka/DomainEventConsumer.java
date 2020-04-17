package cn.tongdun.kunpeng.api.acl.impl.event.notify.kafka;

import cn.tongdun.kunpeng.api.acl.event.notice.IRawDomainEventHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class DomainEventConsumer extends AbstractConsumer {

    private static final Logger                     logger                   = LoggerFactory.getLogger(DomainEventConsumer.class);

    @Autowired
    private IRawDomainEventHandle rawDomainEventHandle;

    @Override
    protected boolean batchConsume() {
        return false;
    }

    @Override
    public void onMessage(String topic, Map message) {
        rawDomainEventHandle.handleRawMessage(message);
    }


}
