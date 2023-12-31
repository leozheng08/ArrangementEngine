package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.constant.ReloadConstant;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventTypeCache;
import cn.tongdun.kunpeng.api.engine.model.eventtype.IEventTypeRepository;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinitionCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.EventTypeEventDO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
public class EventTypeReLoadManager implements IReload<EventTypeEventDO> {

    private Logger logger = LoggerFactory.getLogger(EventTypeReLoadManager.class);

    @Autowired
    private IEventTypeRepository eventTypeRepository;

    @Autowired
    private EventTypeCache eventTypeCache;

    @Autowired
    private FieldDefinitionCache fieldDefinitionCache;

    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init(){
        reloadFactory.register(EventTypeEventDO.class,this);
    }

    @Override
    public boolean create(EventTypeEventDO eventDO){
        return addOrUpdate(eventDO);
    }
    @Override
    public boolean update(EventTypeEventDO eventDO){
        return addOrUpdate(eventDO);
    }
    @Override
    public boolean activate(EventTypeEventDO eventDO){
        return addOrUpdate(eventDO);
    }

    /**
     * 更新事件类型
     * @return
     */
    public boolean addOrUpdate(EventTypeEventDO eventDO){
        String uuid = eventDO.getUuid();
        logger.debug(TraceUtils.getFormatTrace()+"EventType reload start, uuid:{}",uuid);
        try {
            Long timestamp = eventDO.getModifiedVersion();
            EventType eventType = eventTypeCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && eventType != null && timestampCompare(eventType.getModifiedVersion() ,timestamp) >= 0) {
                logger.debug(TraceUtils.getFormatTrace()+"EventType reload localCache is newest, ignore uuid:{}",uuid);
                return true;
            }

            //设置要查询的时间戳，如果redis缓存的时间戳比这新，则直接按redis缓存的数据返回
            ThreadContext.getContext().setAttr(ReloadConstant.THREAD_CONTEXT_ATTR_MODIFIED_VERSION,timestamp);
            EventType newEventType = eventTypeRepository.queryByUuid(uuid);
            if(newEventType == null || !newEventType.isValid()){
                return remove(eventDO);
            }

            eventTypeCache.put(uuid, newEventType);
            fieldDefinitionCache.reloadFieldByEventType(newEventType.getEventCode());
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"EventType reload failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace()+"EventType reload success, uuid:{}",uuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param eventDO
     * @return
     */
    @Override
    public boolean remove(EventTypeEventDO eventDO){
        //事件类型删除或失效后，对应的策略仍可以正常调用，所有缓存中不需要删除
        logger.debug(TraceUtils.getFormatTrace()+"EventType remove ignore, uuid:{}",eventDO.getUuid());
        return true;
    }

    /**
     * 关闭状态
     * @param eventDO
     * @return
     */
    @Override
    public boolean deactivate(EventTypeEventDO eventDO){
        return remove(eventDO);
    }
}
