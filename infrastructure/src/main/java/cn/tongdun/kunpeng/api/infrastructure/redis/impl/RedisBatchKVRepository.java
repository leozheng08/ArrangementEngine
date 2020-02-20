package cn.tongdun.kunpeng.api.infrastructure.redis.impl;

import cn.fraudmetrix.common.client.redis.RedisClient;
import cn.tongdun.kunpeng.share.kv.IBatchKVRepository;
import cn.tongdun.kunpeng.share.kv.IKVResponse;
import cn.tongdun.kunpeng.share.kv.IValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lvyadong on 2020/02/20.
 */
@Component
public class RedisBatchKVRepository implements IBatchKVRepository {

    @Autowired
    private RedisClient kunPengRedisClient;

    @Override
    public List<IValue<String>> batchGet(String... keys) {
        List<String> results = kunPengRedisClient.mget(keys);
        List<IValue<String>> res = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(results)) {
            int size = keys.length;
            for (int i = 0; i < size; i++) {
                String key = keys[i];
                String value = results.get(i);
                res.add(new RedisValue<>(key, value));
            }
        }
        return res;
    }

    @Override
    public List<IValue<String>> batchGet(List<String> keys) {
        List<IValue<String>> res = new ArrayList<>();
        String[] array = new String[keys.size()];
        List<String> results = kunPengRedisClient.mget(keys.toArray(array));
        if (CollectionUtils.isNotEmpty(results)) {
            int size = array.length;
            for (int i = 0; i < size; i++) {
                String key = array[i];
                String value = results.get(i);
                res.add(new RedisValue<>(key, value));
            }
        }
        return res;
    }

    @Override
    public List<IKVResponse> set(List<IValue<String>> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            List<IKVResponse> responses = new ArrayList<>();
            list.forEach(redisValue -> {
                String key = redisValue.getKey();
                String value = redisValue.getValue();
                long ttl = redisValue.getTtl();
                String result = kunPengRedisClient.set(key, value);
                Long result2 = null;
                if (ttl != 0) {
                    result2 = kunPengRedisClient.pexpire(key, ttl);
                }
                IKVResponse response;
                if (StringUtils.containsIgnoreCase(result, "OK") && result2 != null && result2 > 0) {
                    response = new RedisKVResponse(true, result);
                } else {
                    response = new RedisKVResponse(false, result);
                }
                responses.add(response);
            });
            return responses;
        }
        return new ArrayList<>();
    }
}

