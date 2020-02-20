package cn.tongdun.kunpeng.api.infrastructure.redis.impl;

import cn.tongdun.kunpeng.share.kv.IValue;

/**
 * Created by lvyadong on 2020/02/20.
 */
public class RedisValue<T> implements IValue<T> {

    private String key;
    private T value;
    private long ttl;

    public RedisValue() {

    }

    public RedisValue(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public RedisValue(String key, T value, long ttl) {
        this.key = key;
        this.value = value;
        this.ttl = ttl;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public T getValue() {
        return null;
    }

    @Override
    public long getTtl() {
        return 0;
    }
}

