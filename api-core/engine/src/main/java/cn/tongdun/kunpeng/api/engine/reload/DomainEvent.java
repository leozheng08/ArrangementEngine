package cn.tongdun.kunpeng.api.engine.reload;

import lombok.Data;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/3/6 下午12:05
 */
@Data
public class DomainEvent<T> implements IDomainEvent<List<T>>{

    private long occurredTime;

    private String eventType;

    private String entity;

    private List<T> data;


    private Class<T> entityClass;

}
