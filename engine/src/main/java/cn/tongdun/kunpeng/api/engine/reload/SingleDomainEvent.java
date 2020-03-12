package cn.tongdun.kunpeng.api.engine.reload;

import lombok.Data;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/3/6 下午12:05
 */
@Data
public class SingleDomainEvent<T> implements IDomainEvent<T>{

    private long occurredTime;

    private String eventType;

    private String entity;

    private T data;
}
