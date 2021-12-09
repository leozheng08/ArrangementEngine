package cn.tongdun.kunpeng.api.acl.impl.kvcache.redis;

import cn.fraudmetrix.common.client.redis.RedisClient;
import cn.tongdun.kunpeng.share.kv.Cursor;
import cn.tongdun.kunpeng.share.kv.IHashKVRepository;
import cn.tongdun.kunpeng.share.kv.IValue;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RedisHashKVRepository extends RedisKVRepository implements IHashKVRepository {

    @Autowired
    private RedisClient kunPengRedisClient;

    @Override
    public boolean hset(String key, String field, String value) {
        Long res = kunPengRedisClient.hset(key, field, value);
        return res > 0;
    }

    @Override
    public String hget(String key, String field) {
        return kunPengRedisClient.hget(key, field);
    }

    @Override
    public boolean hdel(String key, String... field) {
        Long res = kunPengRedisClient.hdel(key, field);
        return res > 0;
    }

    @Override
    public String hmset(String key, Map<String, String> hash) {
        return kunPengRedisClient.hmset(key, hash);
    }

    @Override
    public String hmset(String key, Map<String, String> hash, long ttlInMilliSeconds) {
        String res = kunPengRedisClient.hmset(key, hash);
        kunPengRedisClient.pexpire(key, ttlInMilliSeconds);
        return res;
    }

    @Override
    public List<String> hmget(final String key, final String... fields){
        return kunPengRedisClient.hmget(key, fields);
    }
    @Override
    public List<IValue<String,String>> hscan(final String key, final Cursor cursor, final int count) {
        ScanParams params = new ScanParams().count(count);
        ScanResult<Map.Entry<String, String>> scanResult = kunPengRedisClient.hscan(key, cursor.getStringCursor(), params);
        if (scanResult != null) {
            cursor.setStringCursor(scanResult.getStringCursor());
            List<Map.Entry<String, String>> results = scanResult.getResult();
            if (CollectionUtils.isNotEmpty(results)) {
                List<IValue<String,String>> res = new ArrayList<>();
                for (Map.Entry<String, String> entry : results) {
                    String resKey = entry.getKey();
                    String resValue = entry.getValue();
                    RedisValue<String> redisValue = new RedisValue<>(resKey, resValue);
                    res.add(redisValue);
                }
                return res;
            }
        }
        return new ArrayList<>(0);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return kunPengRedisClient.hgetAll(key);
    }
}

