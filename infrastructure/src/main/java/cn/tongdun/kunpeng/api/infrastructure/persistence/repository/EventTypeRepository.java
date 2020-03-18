package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;
import cn.tongdun.kunpeng.api.engine.model.eventtype.IEventTypeRepository;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.EventTypeDOMapper;
import cn.tongdun.kunpeng.share.dataobject.EventTypeDO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        List<EventTypeDO> eventTypeDOList= eventTypeDOMapper.selectAll();

        List<EventType> result = new ArrayList<EventType>();
        result = eventTypeDOList.stream().map(eventTypeDO->{
            EventType eventType = new EventType();
            BeanUtils.copyProperties(eventTypeDO,eventType);
            return eventType;
        }).collect(Collectors.toList());

        return result;

    }


    @Override
    public EventType queryByUuid(String uuid){
        EventTypeDO eventTypeDO = eventTypeDOMapper.selectByUuid(uuid);
        if(eventTypeDO == null){
            return null;
        }

        EventType eventType = new EventType();
        BeanUtils.copyProperties(eventTypeDO,eventType);
        return eventType;
    }
}
