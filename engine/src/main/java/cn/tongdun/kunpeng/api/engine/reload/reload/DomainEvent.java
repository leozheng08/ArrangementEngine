package cn.tongdun.kunpeng.api.engine.reload.reload;

import lombok.Data;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/3/6 下午12:05
 */
@Data
public class DomainEvent<T>{

    private long occurredTime;

    private String eventType;

    private String entity;

    private List<T> data;

}
