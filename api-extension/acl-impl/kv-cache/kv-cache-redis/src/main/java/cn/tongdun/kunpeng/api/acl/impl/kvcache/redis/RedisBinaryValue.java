package cn.tongdun.kunpeng.api.acl.impl.kvcache.redis;

import cn.tongdun.kunpeng.share.kv.IValue;

/**
 * Created by lvyadong on 2020/02/20.
 */
public class RedisBinaryValue implements IValue<byte[],byte[]> {

    private byte[] key;
    private byte[] value;
    private long ttl;

    public RedisBinaryValue() {

    }

    public RedisBinaryValue(byte[] key, byte[] value) {
        this.key = key;
        this.value = value;
    }

    public RedisBinaryValue(byte[] key, byte[] value, long ttl) {
        this.key = key;
        this.value = value;
        this.ttl = ttl;
    }

    @Override
    public byte[] getKey() {
        return key;
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    public long getTtl() {
        return ttl;
    }
}

