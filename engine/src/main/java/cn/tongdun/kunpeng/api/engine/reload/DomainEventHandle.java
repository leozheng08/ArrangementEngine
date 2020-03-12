package cn.tongdun.kunpeng.api.engine.reload;

import cn.tongdun.ddd.common.domain.UUIDEntity;
import cn.tongdun.kunpeng.api.engine.reload.impl.EventTypeReLoadManager;
import cn.tongdun.kunpeng.api.engine.reload.impl.PolicyDefinitionReLoadManager;
import cn.tongdun.kunpeng.api.engine.reload.impl.PolicyReLoadManager;
import cn.tongdun.kunpeng.api.engine.reload.impl.RuleReLoadManager;
import cn.tongdun.tdframework.core.concurrent.IThreadService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private PolicyDefinitionReLoadManager policyDefinitionReLoadManager;
    @Autowired
    private PolicyReLoadManager policyReLoadManager;
    @Autowired
    private RuleReLoadManager ruleReLoadManager;
    @Autowired
    private EventTypeReLoadManager eventTypeReLoadManager;
    @Autowired
    private FinishEventCache finishEventCache;
    @Autowired
    private ReloadFactory reloadFactory;

    private final static String EVENT_TYPE_REMOVE ="Remove";


    /**
     * 默认线程池
     *     如果处理器无定制线程池，则使用此默认
     */
    ExecutorService executor;

    public void init(){
        threadPool = threadService.createThreadPool(
                2,
                2,
                30L,
                TimeUnit.MINUTES,
                1000,
                "DomainEventHandle-");
    }


    public void handleMessage(List<String> eventMsgs ){
        if(eventMsgs == null || eventMsgs.isEmpty()){
            return;
        }

        for(String eventMsg : eventMsgs){
            executor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call(){
                    try {
                        SingleDomainEvent singleDomainEvent = eventMsgParser.parseSingleDomainEvent(eventMsg);
                        //如果此消息已处理成功，则不需要再处理
                        if(isFinish(singleDomainEvent)){
                            return true;
                        }

                        boolean result = handleMessage(singleDomainEvent);
                        if (result) {
                            //如果处理成功，则标记到缓存中
                            setFinish(singleDomainEvent);
                        }
                    } catch (Exception e){
                        logger.error("EventNotify reLoadManager is null,class:{},event:{}", eventMsg);
                        return false;
                    }
                    return true;
                }
            });
        }
    }


    private boolean handleMessage(SingleDomainEvent domainEvent){
        try {
                Object entryData = domainEvent.getData();
                IReload reLoadManager = reloadFactory.getReload(entryData.getClass());
                if (reLoadManager == null) {
                    logger.error("EventNotify reLoadManager is null,class:{},event:{}", entryData.getClass(), domainEvent);
                    return false;
                }
                if (domainEvent.getEventType().endsWith(EVENT_TYPE_REMOVE)) {
                    if(!reLoadManager.remove(domainEvent.getData())){
                        return false;
                    }
                } else {
                    if(reLoadManager.addOrUpdate(domainEvent.getData())){
                        return false;
                    }
                }
        } catch (Exception e){
            logger.error("EventNotify reLoadManager is null,class:{},event:{}", domainEvent);
            return false;
        }

        return true;
    }

    /**
     *
     * @param domainEvent
     * @return
     */
    private boolean isFinish(SingleDomainEvent domainEvent){
        UUIDEntity uuidEntity = (UUIDEntity)domainEvent.getData();
        String key = StringUtils.join(domainEvent.getEntity(),SPLIT_CHAR,uuidEntity.getUuid());
        return finishEventCache.contains(key);
    }

    private void setFinish(SingleDomainEvent domainEvent){
        UUIDEntity uuidEntity = (UUIDEntity)domainEvent.getData();
        String key = StringUtils.join(domainEvent.getEntity(),SPLIT_CHAR,uuidEntity.getUuid());
        finishEventCache.put(key,true);
    }

}
