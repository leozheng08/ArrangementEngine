package cn.tongdun.kunpeng.api.engine.reload.reload.impl;

import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventTypeCache;
import cn.tongdun.kunpeng.api.engine.model.eventtype.IEventTypeRepository;
import cn.tongdun.kunpeng.api.engine.reload.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.EventTypeDO;
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
public class EventTypeReLoadManager implements IReload<EventTypeDO> {

    private Logger logger = LoggerFactory.getLogger(EventTypeReLoadManager.class);

    @Autowired
    private IEventTypeRepository eventTypeRepository;

    @Autowired
    private EventTypeCache eventTypeCache;

    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init(){
        reloadFactory.register(EventTypeDO.class,this);
    }

    /**
     * 更新事件类型
     * @return
     */
    @Override
    public boolean addOrUpdate(EventTypeDO eventTypeDO){
        String uuid = eventTypeDO.getUuid();
        logger.info("EventTypeReLoadManager start, uuid:{}",uuid);
        try {
            Long timestamp = eventTypeDO.getGmtModify().getTime();
            EventType eventType = eventTypeCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(eventType != null && eventType.getModifiedVersion() >= timestamp) {
                return true;
            }

            EventType newEventType = eventTypeRepository.queryByUuid(uuid);
            eventTypeCache.put(uuid, newEventType);
        } catch (Exception e){
            logger.error("EventTypeReLoadManager failed, uuid:{}",uuid,e);
            return false;
        }
        logger.info("EventTypeReLoadManager success, uuid:{}",uuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param uuid
     * @return
     */
    @Override
    public boolean remove(String uuid){
        try {
            eventTypeCache.remove(uuid);
        } catch (Exception e){
            return false;
        }
        return true;
    }



}
