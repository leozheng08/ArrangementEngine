package cn.tongdun.kunpeng.api.acl.impl.kvcache.redis;

import cn.tongdun.kunpeng.share.kv.Cursor;
import cn.tongdun.kunpeng.share.kv.IHashBinaryKVRepository;
import cn.tongdun.kunpeng.share.kv.IValue;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lvyadong on 2020/02/20.
 */
@Component
public class RedisHashBinaryKVRepository extends RedisBinaryKVRepository implements IHashBinaryKVRepository {


    @Override
    public boolean hset(final byte[] key, final byte[] field, final byte[] value) {
        Long res = kunPengRedisClient.hset(key, field, value);
        return res > 0;
    }

    @Override
    public final byte[] hget(final byte[] key, final byte[] field) {
        return kunPengRedisClient.hget(key, field);
    }

    @Override
    public boolean hdel(final byte[] key, final byte[]... field) {
        Long res = kunPengRedisClient.hdel(key, field);
        return res > 0;
    }

    @Override
    public String hmset(final byte[] key, final Map<byte[], byte[]> hash) {
        return kunPengRedisClient.hmset(key, hash);
    }

    @Override
    public String hmset(final byte[] key, final Map<byte[], byte[]> hash, long ttlInMilliSeconds) {
        String res = kunPengRedisClient.hmset(key, hash);
        kunPengRedisClient.pexpire(key, ttlInMilliSeconds);
        return res;
    }

    @Override
    public List<IValue<byte[],byte[]>> hscan(final byte[] prefix, final Cursor cursor, final int count) {
        ScanParams params = new ScanParams().count(count);
        ScanResult<Map.Entry<byte[], byte[]>> scanResult = kunPengRedisClient.hscan(prefix,
                cursor.getStringCursor().getBytes(), params);
        if (scanResult != null) {
            cursor.setStringCursor(scanResult.getStringCursor());
            List<Map.Entry<byte[], byte[]>> results = scanResult.getResult();
            if (CollectionUtils.isNotEmpty(results)) {
                List<IValue<byte[],byte[]>> res = new ArrayList<>();
                for (Map.Entry<byte[], byte[]> entry : results) {
                    byte[] resKey = entry.getKey();
                    byte[] resValue = entry.getValue();
                    RedisBinaryValue redisValue = new RedisBinaryValue(resKey, resValue);
                    res.add(redisValue);
                }
                return res;
            }
        }
        return new ArrayList<>(0);
    }

    @Override
    public Map<byte[], byte[]> hgetAll(final byte[] key) {
        return kunPengRedisClient.hgetAll(key);
    }
}

