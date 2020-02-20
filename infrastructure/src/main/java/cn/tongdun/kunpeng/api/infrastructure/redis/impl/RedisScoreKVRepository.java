package cn.tongdun.kunpeng.api.infrastructure.redis.impl;

import cn.fraudmetrix.common.client.redis.RedisClient;
import cn.tongdun.kunpeng.share.kv.Cursor;
import cn.tongdun.kunpeng.share.kv.IScoreKVRepository;
import cn.tongdun.kunpeng.share.kv.IScoreValue;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lvyadong on 2020/02/20.
 */
@Component
public class RedisScoreKVRepository implements IScoreKVRepository {

    @Autowired
    private RedisClient kunPengRedisClient;

    @Override
    public boolean zadd(String key, double score, String member) {
        Long res = kunPengRedisClient.zadd(key, score, member);
        return res != null && res > 0;
    }

    @Override
    public boolean zadd(String key, Map<String, Double> scoreMembers) {
        Long res = kunPengRedisClient.zadd(key, scoreMembers);
        return res != null && res > 0;
    }

    @Override
    public Double zscore(String key, String member) {
        return kunPengRedisClient.zscore(key, member);
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        return kunPengRedisClient.zrangeByScore(key, min, max);
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        return kunPengRedisClient.zrangeByScore(key, min, max);
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        return kunPengRedisClient.zrangeByScore(key, min, max, offset, count);
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        return kunPengRedisClient.zrangeByScore(key, min, max, offset, count);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        return kunPengRedisClient.zrevrangeByScore(key, max, min);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min) {
        return kunPengRedisClient.zrevrangeByScore(key, max, min);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        return kunPengRedisClient.zrevrangeByScore(key, max, min, offset, count);
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        return kunPengRedisClient.zrevrangeByScore(key, max, min, offset, count);
    }

    @Override
    public Set<IScoreValue> zrangeByScoreWithScores(String key, double min, double max) {
        Set<Tuple> tuples = kunPengRedisClient.zrangeByScoreWithScores(key, min, max);
        if (CollectionUtils.isNotEmpty(tuples)) {
            Set<IScoreValue> values = new HashSet<>(tuples.size());
            tuples.forEach(tuple -> {
                IScoreValue<String> redisScoreValue = RedisScoreValue.convert(key, tuple);
                values.add(redisScoreValue);
            });
            return values;
        }
        return new HashSet<>(0);
    }

    @Override
    public Set<IScoreValue> zrangeByScoreWithScores(String key, String min, String max) {
        Set<Tuple> tuples = kunPengRedisClient.zrangeByScoreWithScores(key, min, max);
        if (CollectionUtils.isNotEmpty(tuples)) {
            Set<IScoreValue> values = new HashSet<>(tuples.size());
            tuples.forEach(tuple -> {
                IScoreValue<String> redisScoreValue = RedisScoreValue.convert(key, tuple);
                values.add(redisScoreValue);
            });
            return values;
        }
        return new HashSet<>(0);
    }

    @Override
    public Set<IScoreValue> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        Set<Tuple> tuples = kunPengRedisClient.zrangeByScoreWithScores(key, min, max, offset, count);
        if (CollectionUtils.isNotEmpty(tuples)) {
            Set<IScoreValue> values = new HashSet<>(tuples.size());
            tuples.forEach(tuple -> {
                IScoreValue<String> redisScoreValue = RedisScoreValue.convert(key, tuple);
                values.add(redisScoreValue);
            });
            return values;
        }
        return new HashSet<>(0);
    }

    @Override
    public Set<IScoreValue> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        Set<Tuple> tuples = kunPengRedisClient.zrangeByScoreWithScores(key, min, max, offset, count);
        if (CollectionUtils.isNotEmpty(tuples)) {
            Set<IScoreValue> values = new HashSet<>(tuples.size());
            tuples.forEach(tuple -> {
                IScoreValue<String> redisScoreValue = RedisScoreValue.convert(key, tuple);
                values.add(redisScoreValue);
            });
            return values;
        }
        return new HashSet<>(0);
    }

    @Override
    public Set<IScoreValue> zrevrangeByScoreWithScores(String key, double max, double min) {
        Set<Tuple> tuples = kunPengRedisClient.zrevrangeByScoreWithScores(key, max, min);
        if (CollectionUtils.isNotEmpty(tuples)) {
            Set<IScoreValue> values = new HashSet<>(tuples.size());
            tuples.forEach(tuple -> {
                IScoreValue<String> redisScoreValue = RedisScoreValue.convert(key, tuple);
                values.add(redisScoreValue);
            });
            return values;
        }
        return new HashSet<>(0);
    }

    @Override
    public Set<IScoreValue> zrevrangeByScoreWithScores(String key, String max, String min) {
        Set<Tuple> tuples = kunPengRedisClient.zrevrangeByScoreWithScores(key, max, min);
        if (CollectionUtils.isNotEmpty(tuples)) {
            Set<IScoreValue> values = new HashSet<>(tuples.size());
            tuples.forEach(tuple -> {
                IScoreValue<String> redisScoreValue = RedisScoreValue.convert(key, tuple);
                values.add(redisScoreValue);
            });
            return values;
        }
        return new HashSet<>(0);
    }

    @Override
    public Set<IScoreValue> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        Set<Tuple> tuples = kunPengRedisClient.zrevrangeByScoreWithScores(key, max, min, offset, count);
        if (CollectionUtils.isNotEmpty(tuples)) {
            Set<IScoreValue> values = new HashSet<>(tuples.size());
            tuples.forEach(tuple -> {
                IScoreValue<String> redisScoreValue = RedisScoreValue.convert(key, tuple);
                values.add(redisScoreValue);
            });
            return values;
        }
        return new HashSet<>(0);
    }

    @Override
    public Set<IScoreValue> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        Set<Tuple> tuples = kunPengRedisClient.zrevrangeByScoreWithScores(key, max, min, offset, count);
        if (CollectionUtils.isNotEmpty(tuples)) {
            Set<IScoreValue> values = new HashSet<>(tuples.size());
            tuples.forEach(tuple -> {
                IScoreValue<String> redisScoreValue = RedisScoreValue.convert(key, tuple);
                values.add(redisScoreValue);
            });
            return values;
        }
        return new HashSet<>(0);
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max) {
        return kunPengRedisClient.zrangeByLex(key, min, max);
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        return kunPengRedisClient.zrangeByLex(key, min, max, offset, count);
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min) {
        return kunPengRedisClient.zrevrangeByScore(key, max, min);
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        return kunPengRedisClient.zrevrangeByScore(key, max, min, offset, count);
    }

    @Override
    public boolean zremrangeByLex(String key, String min, String max) {
        Long res = kunPengRedisClient.zremrangeByLex(key, min, max);
        return res != null && res > 0;
    }

    @Override
    public Set<IScoreValue<String>> zscan(String key, Cursor cursor, int count) {
        ScanParams scanParams = new ScanParams().count(count);
        ScanResult<Tuple> tupleScanResult = kunPengRedisClient.zscan(key, cursor.getStringCursor(), scanParams);
        if (tupleScanResult != null) {
            cursor.setStringCursor(tupleScanResult.getStringCursor());
            List<Tuple> results = tupleScanResult.getResult();
            if (CollectionUtils.isNotEmpty(results)) {
                Set<IScoreValue<String>> values = new HashSet<>(results.size());
                results.forEach(tuple -> {
                    IScoreValue<String> redisScoreValue = RedisScoreValue.convert(key, tuple);
                    values.add(redisScoreValue);
                });
                return values;
            }
        }
        return new HashSet<>(0);
    }

    @Override
    public Long zcard(String key) {
        return kunPengRedisClient.zcard(key);
    }
}

