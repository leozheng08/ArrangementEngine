package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventTypeCache;
import cn.tongdun.kunpeng.api.engine.model.eventtype.IEventTypeRepository;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.DynamicScriptDO;
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
        logger.debug("EventType reload start, uuid:{}",uuid);
        try {
            Long timestamp = eventTypeDO.getGmtModify().getTime();
            EventType eventType = eventTypeCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && eventType != null && eventType.getModifiedVersion() >= timestamp) {
                logger.debug("EventType reload localCache is newest, ignore uuid:{}",uuid);
                return true;
            }

            EventType newEventType = eventTypeRepository.queryByUuid(uuid);
            if(newEventType == null){
                return remove(eventTypeDO);
            }

            eventTypeCache.put(uuid, newEventType);
        } catch (Exception e){
            logger.error("EventType reload failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug("EventType reload success, uuid:{}",uuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param eventTypeDO
     * @return
     */
    @Override
    public boolean remove(EventTypeDO eventTypeDO){
        try {
            eventTypeCache.remove(eventTypeDO.getUuid());
        } catch (Exception e){
            logger.error("EventType remove failed, uuid:{}",eventTypeDO.getUuid(),e);
            return false;
        }
        logger.debug("EventType remove success, uuid:{}",eventTypeDO.getUuid());
        return true;
    }

    /**
     * 关闭状态
     * @param eventTypeDO
     * @return
     */
    @Override
    public boolean deactivate(EventTypeDO eventTypeDO){
        return remove(eventTypeDO);
    }
}
