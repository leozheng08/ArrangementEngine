package cn.tongdun.kunpeng.api.engine.reload;

import lombok.Data;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/3/6 下午12:05
 */
public interface IDomainEvent<T>{

    public long getOccurredTime();

    public void setOccurredTime(long occurredTime) ;

    public String getEventType();

    public void setEventType(String eventType) ;

    public String getEntity() ;

    public void setEntity(String entity) ;

    public T getData();

    public void setData(T data) ;
}
