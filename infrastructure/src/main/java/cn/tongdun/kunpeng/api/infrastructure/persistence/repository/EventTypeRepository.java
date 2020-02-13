package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;
import cn.tongdun.kunpeng.api.engine.model.eventtype.IEventTypeRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.EventTypeDOMapper;
import cn.tongdun.kunpeng.share.dataobject.EventTypeDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
@Repository
public class EventTypeRepository implements IEventTypeRepository {


    @Autowired
    private EventTypeDOMapper eventTypeDOMapper;


    @Override
    public List<EventType> queryAll(){

        List<EventTypeDO> eventTypeDOList= eventTypeDOMapper.selectAllAvailable();

        List<EventType> resultEventTypeList = new ArrayList<EventType>();
        for (EventTypeDO eventTypeDO : eventTypeDOList) {
            EventType eventType = new EventType();
            eventType.setCode(eventTypeDO.getEventCode());
            eventType.setName(eventTypeDO.getEventName());
            resultEventTypeList.add(eventType);
        }

        return resultEventTypeList;

    }
}
