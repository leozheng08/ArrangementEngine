package cn.tongdun.kunpeng.api.application.activity.common;

import cn.tongdun.kunpeng.api.common.data.QueueItem;
import cn.tongdun.kunpeng.api.engine.model.dictionary.DictionaryManager;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

/**
 * activity发kafka
 *
 * @Author: liang.chen
 * @Date: 2020/3/3 下午7:42
 */
@Component
public class ActivityStoreKafkaWorker implements IEventWorker {

    private static Logger logger = LoggerFactory.getLogger(ActivityStoreKafkaWorker.class);

    @Autowired
    private ExtensionExecutor extensionExecutor;

    @Autowired
    private IMsgProducer msgProducer;

    @Autowired
    DictionaryManager dictionaryManager;

    @Value("${kafka.kunpeng.activity.topic:kunpeng_api_raw_activity}")
    private String KUNPENG_API_RAW_ACTIVITY;

    @Value("${kafka.kunpeng.activity.challenge.topic:${kafka.kunpeng.activity.topic:kunpeng_api_raw_activity}}")
    private String KUNPENG_API_RAW_CHALLENGER_ACTIVITY;

    @Value("${no.send.kafka.partnerCode:}")
    private String partnerCodes;

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }


    //过滤条件,在入队列之前过滤
    @Override
    public Predicate<QueueItem> getFilter() {
        return (item) -> {
            //过滤掉测试标记的
            if (item.getContext().isTestFlag()) {
                return false;
            }
            //过滤掉调用失败无效的数据
            if (!item.getResponse().isSuccess()) {
                return false;
            }
            return true;
        };
    }

    @Override
    public void onEvent(QueueItem item) {

        if (item.getContext() == null) {
            return;
        }

        //gateway 压测白名单，以下合作方无需发送消息
        if(notSendKafka().contains(item.getContext().getPartnerCode())){
            return;
        }
        //生成activity消息
        IActitivyMsg actitivyMsg = generateActivity(item);

        //发送到kafka originalSeqId发送不同的topic
        Object originalSeqId = item.getContext().getFieldValues().get("originalSeqId");
        if (Objects.nonNull(originalSeqId)) {
            sendChallengerToKafka(actitivyMsg);
        } else {
            sendToKafka(actitivyMsg);
        }
    }


    private IActitivyMsg generateActivity(QueueItem item) {
        IActitivyMsg actitivyMsg = extensionExecutor.execute(IGenerateActivityExtPt.class, item.getContext().getBizScenario(),
                extension -> extension.generateActivity(item));
        return actitivyMsg;
    }

    private void sendToKafka(IActitivyMsg actitivyMsg) {
//        logger.info("GenerateActivityExt....................msgKey={}", actitivyMsg.getMessageKey());
        msgProducer.produce(KUNPENG_API_RAW_ACTIVITY, actitivyMsg.getMessageKey(), actitivyMsg.toJsonString());
    }

    private void sendChallengerToKafka(IActitivyMsg actitivyMsg) {
//        logger.info("GenerateActivityExt....................msgKey={}", actitivyMsg.getMessageKey());
        msgProducer.produce(KUNPENG_API_RAW_CHALLENGER_ACTIVITY, actitivyMsg.getMessageKey(), actitivyMsg.toJsonString());
    }

    private Set<String> notSendKafka(){
        Set<String> set = new HashSet<>();
        if(StringUtils.isNotEmpty(partnerCodes)){
            return new HashSet<>(Arrays.asList(partnerCodes.split(",")));
        }
        return set;
    }
}
