package cn.tongdun.kunpeng.api.acl.impl.kvcache.redis;

import cn.tongdun.kunpeng.share.kv.IScoreValue;
import redis.clients.jedis.Tuple;

/**
 * Created by lvyadong on 2020/02/20.
 */
public class RedisScoreValue<T> extends RedisValue<T> implements IScoreValue<T> {

    private double score;

    public RedisScoreValue(String key, T value, double score) {
        super(key, value);
        this.score = score;
    }

    @Override
    public Double getScore() {
        return score;
    }

    public static RedisScoreValue convert(String key, Tuple tuple) {
        return new RedisScoreValue<>(key, tuple.getElement(), tuple.getScore());
    }
}

