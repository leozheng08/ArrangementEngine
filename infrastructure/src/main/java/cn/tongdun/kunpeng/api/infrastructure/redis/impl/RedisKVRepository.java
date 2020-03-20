package cn.tongdun.kunpeng.api.infrastructure.redis.impl;

import cn.fraudmetrix.common.client.redis.RedisClient;
import cn.tongdun.kunpeng.share.kv.Cursor;
import cn.tongdun.kunpeng.share.kv.IKVRepository;
import cn.tongdun.kunpeng.share.kv.IValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by lvyadong on 2020/02/20.
 */
@Component
public class RedisKVRepository implements IKVRepository {

    @Autowired
    protected RedisClient kunPengRedisClient;

    @Override
    public boolean exists(String key) {
        return kunPengRedisClient.exists(key);
    }

    @Override
    public boolean del(String key) {
        Long res = kunPengRedisClient.del(key);
        return res != null && res > 0;
    }

    @Override
    public String get(String key) {
        return kunPengRedisClient.get(key);
    }

    @Override
    public boolean set(String key, String value) {
        String reply = kunPengRedisClient.set(key, value);
        return StringUtils.containsIgnoreCase(reply, "OK");
    }

    @Override
    public boolean set(String key, String value, long ttlInMilliSeconds) {
        String reply = kunPengRedisClient.set(key, value);
        if (!StringUtils.containsIgnoreCase(reply, "OK")) {
            return false;
        }
        Long res = kunPengRedisClient.pexpire(key, ttlInMilliSeconds);
        return res > 0;
    }

    @Override
    public long ttl(String key) {
        return kunPengRedisClient.pttl(key);
    }

    @Override
    public boolean setTtl(String key, long ttlInMilliSeconds) {
        Long res = kunPengRedisClient.pexpire(key, ttlInMilliSeconds);
        return res > 0;
    }

    @Override
    public List<IValue<String>> scan(String prefix, Cursor cursor, int count) {
        return null;
    }

    @Override
    public void destroy() {

    }
}

