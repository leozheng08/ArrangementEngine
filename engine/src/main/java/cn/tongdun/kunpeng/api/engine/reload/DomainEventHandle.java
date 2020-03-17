package cn.tongdun.kunpeng.api.engine.reload;

import cn.tongdun.ddd.common.domain.CommonEntity;
import cn.tongdun.kunpeng.api.engine.model.constant.DomainEventTypeEnum;
import cn.tongdun.tdframework.core.concurrent.IThreadService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author: liang.chen
 * @Date: 2020/3/12 下午1:39
 */
@Component
public class DomainEventHandle {

    private Logger logger = LoggerFactory.getLogger(DomainEventHandle.class);
    public static final String SPLIT_CHAR = "^^";
    @Autowired
    private IThreadService threadService;

    private ExecutorService threadPool;


    @Autowired
    private EventMsgParser eventMsgParser;
    @Autowired
    private FinishEventCache finishEventCache;
    @Autowired
    private ReloadFactory reloadFactory;

    private final static String EVENT_TYPE_REMOVE ="Remove";


    @PostConstruct
    public void init(){
        threadPool = threadService.createThreadPool(
                1,
                2,
                30L,
                TimeUnit.MINUTES,
                10000,
                "DomainEventHandle-");
    }


    /**
     * 根据redis或aerospike远程缓存中拉取最近两分钟的变更数据，处理这些变更事件
     * @param eventMsgs
     */
    public void handleMessage(List<String> eventMsgs ){
        if(eventMsgs == null || eventMsgs.isEmpty()){
            return;
        }

        for(String eventMsg : eventMsgs){
            threadPool.submit(new Callable<Boolean>() {
                @Override
                public Boolean call(){
                    try {
                        SingleDomainEvent singleDomainEvent = eventMsgParser.parseSingleDomainEvent(eventMsg);
                        if(singleDomainEvent == null){
                            return true;
                        }
                        //如果此消息已处理成功，则不需要再处理
                        if(isFinish(singleDomainEvent)){
                            return true;
                        }

                        boolean result = handleMessage(singleDomainEvent);
                        if (result) {
                            //如果处理成功，则标记到本地缓存中
                            setFinish(singleDomainEvent);
                        }
                    } catch (Exception e){
                        logger.error("handleMessage error,event:{}", eventMsg,e);
                        return false;
                    }
                    return true;
                }
            });
        }
    }


    /**
     * 处理单个事件
     * @param domainEvent
     * @return
     */
    private boolean handleMessage(SingleDomainEvent domainEvent){
        try {
                Object entryData = domainEvent.getData();
                IReload reLoadManager = reloadFactory.getReload(entryData.getClass());
                if (reLoadManager == null) {
                    logger.warn("reLoadManager is null,class:{},event:{}", entryData.getClass(), domainEvent);
                    return true;
                }
                if (domainEvent.getEventType().toUpperCase().endsWith(DomainEventTypeEnum.REMOVE.name())) {
                    if(!reLoadManager.remove(domainEvent.getData())){
                        return false;
                    }
                } else if (domainEvent.getEventType().toUpperCase().endsWith(DomainEventTypeEnum.DEACTIVATE.name())) {
                    if(!reLoadManager.deactivate(domainEvent.getData())){
                        return false;
                    }
                } else {
                    if(!reLoadManager.addOrUpdate(domainEvent.getData())){
                        return false;
                    }
                }
        } catch (Exception e){
            logger.error("handleMessage error,event:{}", domainEvent,e);
            return false;
        }

        return true;
    }

    /**
     * 判断某个事件是否已处理过，避免重复处理
     * @param domainEvent
     * @return
     */
    private boolean isFinish(SingleDomainEvent domainEvent){
        String key = buildKey(domainEvent);
        return finishEventCache.contains(key);
    }

    private String buildKey(SingleDomainEvent domainEvent){
        String uuid = null;
        Long gmtModify = domainEvent.getOccurredTime();
        if(domainEvent.getData() instanceof CommonEntity) {
            CommonEntity commonEntity = (CommonEntity) domainEvent.getData();
            uuid = commonEntity.getUuid();
            if(gmtModify<commonEntity.getGmtModify().getTime()) {
                gmtModify = commonEntity.getGmtModify().getTime();
            }
        }


        return StringUtils.join(domainEvent.getEntity(),SPLIT_CHAR,uuid,SPLIT_CHAR,gmtModify);
    }

    /**
     * 成功处理的，标记到本地缓存
     * @param domainEvent
     */
    private void setFinish(SingleDomainEvent domainEvent){
        String key = buildKey(domainEvent);
        finishEventCache.put(key,true);
    }

}
