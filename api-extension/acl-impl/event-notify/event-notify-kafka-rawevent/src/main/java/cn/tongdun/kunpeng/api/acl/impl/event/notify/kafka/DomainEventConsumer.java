package cn.tongdun.kunpeng.api.acl.impl.event.notify.kafka;

import cn.tongdun.kunpeng.api.acl.event.notice.IRawDomainEventHandle;
import cn.tongdun.kunpeng.share.json.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DomainEventConsumer extends AbstractConsumer {

    private static final Logger                     logger                   = LoggerFactory.getLogger(DomainEventConsumer.class);

    @Autowired
    private IRawDomainEventHandle rawDomainEventHandle;

    @Value("${domain.event.topic2.disabled:true}")
    private String topic2Disabled;

    @Value("${domain.event.topic2:kunpeng_admin_event2,staging_kunpeng_admin_event2}")
    private String topic2;

    private boolean isTopic2Disabled(){
        return Boolean.parseBoolean(topic2Disabled);
    }

    private List<String> topic2List(){
        if(StringUtils.isNotBlank(topic2)){
            return Lists.newArrayList(topic2.split(","));
        }else {
            return new ArrayList<String>();
        }
    }

    @Override
    protected boolean batchConsume() {
        return false;
    }

    @Override
    public void onMessage(String topic, Map message) {
        logger.info("DomainEventConsumer consumer message,topic:"+topic+",message:"+ JSON.toJSONString(message));
        if(isTopic2Disabled() && topic2List().contains(topic)){
            logger.info("topic2:{} ignore, message:{}", topic, message);
            return;
        }
        rawDomainEventHandle.handleRawMessage(message);
    }


}
