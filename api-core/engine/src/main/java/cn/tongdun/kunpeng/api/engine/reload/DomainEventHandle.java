package cn.tongdun.kunpeng.api.engine.reload;

import cn.tongdun.kunpeng.api.common.data.DomainEventTypeEnum;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.EventDO;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.IThreadService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
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
                        DomainEvent domainEvent = eventMsgParser.parse(eventMsg);
                        if(domainEvent == null){
                            return true;
                        }
                        //如果此消息已处理成功，则不需要再处理
                        if(isFinish(domainEvent)){
                            return true;
                        }

                        boolean result = handleMessage(domainEvent);
                        if (result) {
                            //如果处理成功，则标记到本地缓存中
                            setFinish(domainEvent);
                        }
                    } catch (Exception e){
                        logger.error(TraceUtils.getFormatTrace()+"handleMessage error,event:{}", eventMsg,e);
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
    private boolean handleMessage(DomainEvent domainEvent){
        try {
                Object entryData = domainEvent.getData();
                IReload reLoadManager = reloadFactory.getReload(domainEvent.getEntityClass());
                if (reLoadManager == null) {
                    logger.warn(TraceUtils.getFormatTrace()+"reLoadManager is null,class:{},event:{}", entryData.getClass(), domainEvent);
                    return true;
                }
                if (domainEvent.getEventType().toUpperCase().endsWith(DomainEventTypeEnum.CREATE.name())) {
                    if(!reLoadManager.create(domainEvent.getData())){
                        return false;
                    }
                } else if (domainEvent.getEventType().toUpperCase().endsWith(DomainEventTypeEnum.DEACTIVATE.name())) {
                    if(!reLoadManager.deactivate(domainEvent.getData())){
                        return false;
                    }
                } else if (domainEvent.getEventType().toUpperCase().endsWith(DomainEventTypeEnum.ACTIVATE.name())) {
                    if(!reLoadManager.activate(domainEvent.getData())){
                        return false;
                    }
                }  else if (domainEvent.getEventType().toUpperCase().endsWith(DomainEventTypeEnum.UPDATE.name())) {
                    if(!reLoadManager.update(domainEvent.getData())){
                        return false;
                    }
                } else if (domainEvent.getEventType().toUpperCase().endsWith(DomainEventTypeEnum.REMOVE.name())) {
                    if(!reLoadManager.remove(domainEvent.getData())){
                        return false;
                    }
                } else if (domainEvent.getEventType().toUpperCase().endsWith(DomainEventTypeEnum.SORT.name())) {
                    if(!reLoadManager.sort(domainEvent.getData())){
                        return false;
                    }
                } else if (domainEvent.getEventType().toUpperCase().endsWith(DomainEventTypeEnum.SUSPEND.name())) {
                    List list = domainEvent.getData();
                    if(list == null || list.isEmpty()){
                        return true;
                    }
                    if(!reLoadManager.suspend(list.get(0))){
                        return false;
                    }
                } else if (domainEvent.getEventType().toUpperCase().endsWith(DomainEventTypeEnum.TERMINATE.name())) {
                    List list = domainEvent.getData();
                    if(list == null || list.isEmpty()){
                        return true;
                    }
                    if(!reLoadManager.terminate(list.get(0))){
                        return false;
                    }
                } else if (domainEvent.getEventType().toUpperCase().endsWith(DomainEventTypeEnum.SWITCH_DECISION_MODE.name())) {
                    List list = domainEvent.getData();
                    if(list == null || list.isEmpty()){
                        return true;
                    }
                    if(!reLoadManager.switchDecisionMode(list.get(0))){
                        return false;
                    }
                } else {
                    if(!reLoadManager.update(domainEvent.getData())){
                        return false;
                    }
                }
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"handleMessage error,event:{}", domainEvent,e);
            return false;
        }

        return true;
    }

    /**
     * 判断某个事件是否已处理过，避免重复处理
     * @param domainEvent
     * @return
     */
    private boolean isFinish(DomainEvent domainEvent){

        List list = domainEvent.getData();
        if(list == null || list.isEmpty()) {
            return true;
        }
        Long gmtModify = domainEvent.getOccurredTime();
        for(Object obj:list) {
            String uuid = null;
            Long gmtModifyTmp = null;
            if(obj instanceof Map){
                uuid = JsonUtil.getString((Map)obj,"uuid");
                gmtModifyTmp = JsonUtil.getLong((Map)obj,"gmtModify");
            } else if(obj instanceof EventDO) {
                EventDO domainEventDO = (EventDO) obj;
                uuid = domainEventDO.getUuid();
                if(domainEventDO.getGmtModify() != null) {
                    gmtModifyTmp = domainEventDO.getGmtModify().getTime();
                }
            } else {
                logger.warn(TraceUtils.getFormatTrace()+"不能识别的对象 ,eventType:{}, obj:{}",domainEvent.getEventType(),obj);
                continue;
            }

            if(gmtModifyTmp == null){
                gmtModifyTmp = gmtModify;
            }

            String key = buildKey(domainEvent.getEntity(),uuid,gmtModifyTmp);
            if(!finishEventCache.contains(key)){
                return false;
            }
        }

        return true;
    }

    /**
     * 生成的key为：entityName+uuid+version(即时间戳)
     * @return
     */
    private String buildKey(String entity, String uuid,Long gmtModify){
        return StringUtils.join(entity,SPLIT_CHAR,uuid,SPLIT_CHAR,gmtModify);
    }

    /**
     * 成功处理的，标记到本地缓存
     * @param domainEvent
     */
    private void setFinish(DomainEvent domainEvent){
        List list = domainEvent.getData();
        if(list == null || list.isEmpty()) {
            return;
        }
        Long gmtModify = domainEvent.getOccurredTime();
        for(Object obj:list) {
            String uuid = null;
            Long gmtModifyTmp = null;
            if(obj instanceof Map){
                uuid = JsonUtil.getString((Map)obj,"uuid");
                gmtModifyTmp = JsonUtil.getLong((Map)obj,"gmtModify");
            } else if(obj instanceof EventDO) {
                EventDO domainEventDO = (EventDO) obj;
                uuid = domainEventDO.getUuid();
                if(domainEventDO.getGmtModify() != null) {
                    gmtModifyTmp = domainEventDO.getGmtModify().getTime();
                }
            } else {
                logger.warn(TraceUtils.getFormatTrace()+"不能识别的对象,eventType:{}, obj:{}",domainEvent.getEventType(),obj);
                continue;
            }

            if(gmtModifyTmp == null){
                gmtModifyTmp = gmtModify;
            }

            String key = buildKey(domainEvent.getEntity(),uuid,gmtModifyTmp);
            finishEventCache.put(key,true);
        }
    }

}
