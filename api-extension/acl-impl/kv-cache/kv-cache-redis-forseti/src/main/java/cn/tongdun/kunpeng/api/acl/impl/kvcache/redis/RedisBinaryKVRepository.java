package cn.tongdun.kunpeng.api.acl.impl.kvcache.redis;

import cn.fraudmetrix.common.client.redis.BinaryRedisClient;
import cn.tongdun.kunpeng.share.kv.Cursor;
import cn.tongdun.kunpeng.share.kv.IBinaryKVRepository;
import cn.tongdun.kunpeng.share.kv.IValue;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lvyadong on 2020/02/20.
 */
public class RedisBinaryKVRepository implements IBinaryKVRepository {

    @Resource(name = "kunPengRedisClient")
    protected BinaryRedisClient kunPengRedisClient;

    @Override
    public boolean exists(final byte[] key){
        return kunPengRedisClient.exists(key);
    }

    @Override
    public boolean del(final byte[] key) {
        Long res = kunPengRedisClient.del(key);
        return res != null && res > 0;
    }

    @Override
    public byte[] get(final byte[] key){
        return kunPengRedisClient.get(key);
    }

    @Override
    public boolean set(final byte[] key, final byte[] value){
        String reply = kunPengRedisClient.set(key, value);
        return StringUtils.containsIgnoreCase(reply, "OK");
    }

    @Override
    public boolean set(final byte[] key, final byte[] value, final long ttlInMilliSeconds){
        String reply = kunPengRedisClient.set(key, value);
        if (!StringUtils.containsIgnoreCase(reply, "OK")) {
            return false;
        }
        Long res = kunPengRedisClient.pexpire(key, ttlInMilliSeconds);
        return res > 0;
    }

    @Override
    public long ttl(final byte[] key){
        return kunPengRedisClient.pttl(key);
    }

    @Override
    public boolean setTtl(final byte[] key, final long ttlInMilliSeconds){
        Long res = kunPengRedisClient.pexpire(key, ttlInMilliSeconds);
        return res > 0;
    }

    @Override
    public List<IValue<byte[],byte[]>> scan(final byte[] prefix, final Cursor cursor, final int count){
        return null;
    }

    @Override
    public void destroy(){

    }
}

