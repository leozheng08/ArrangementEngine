package cn.tongdun.kunpeng.api.acl.impl.kvcache.ignore;

import cn.tongdun.kunpeng.share.kv.Cursor;
import cn.tongdun.kunpeng.share.kv.IScoreKVRepository;
import cn.tongdun.kunpeng.share.kv.IScoreValue;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


@Component
public class IgnoreScoreKVRepository extends IgnoreKVRepository implements IScoreKVRepository {
    private static final Set EMPTY_SET = new LinkedHashSet<>(0);

    @Override
    public boolean zadd(String key, double score, String member) {
        return false;
    }

    @Override
    public boolean zadd(String key, Map<String, Double> scoreMembers) {
        return false;
    }

    @Override
    public Double zscore(String key, String member) {
        return 0D;
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        return EMPTY_SET;
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        return EMPTY_SET;
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        return EMPTY_SET;
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        return EMPTY_SET;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        return EMPTY_SET;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min) {
        return EMPTY_SET;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        return EMPTY_SET;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        return EMPTY_SET;
    }

    @Override
    public Set<IScoreValue> zrangeByScoreWithScores(String key, double min, double max) {
        return EMPTY_SET;
    }

    @Override
    public Set<IScoreValue> zrangeByScoreWithScores(String key, String min, String max) {
        return EMPTY_SET;
    }

    @Override
    public Set<IScoreValue> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        return EMPTY_SET;
    }

    @Override
    public Set<IScoreValue> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        return EMPTY_SET;
    }

    @Override
    public Set<IScoreValue> zrevrangeByScoreWithScores(String key, double max, double min) {
        return EMPTY_SET;
    }

    @Override
    public Set<IScoreValue> zrevrangeByScoreWithScores(String key, String max, String min) {
        return EMPTY_SET;
    }

    @Override
    public Set<IScoreValue> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        return EMPTY_SET;
    }

    @Override
    public Set<IScoreValue> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        return EMPTY_SET;
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max) {
        return EMPTY_SET;
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        return EMPTY_SET;
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min) {
        return EMPTY_SET;
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        return EMPTY_SET;
    }

    @Override
    public boolean zrem(final String key, String... member){
        return false;
    }

    @Override
    public boolean zremrangeByLex(String key, String min, String max) {
        return false;
    }

    @Override
    public Set<IScoreValue<String>> zscan(String key, Cursor cursor, int count) {
        return EMPTY_SET;
    }

    @Override
    public Long zcard(String key) {
        return 0L;
    }
}

