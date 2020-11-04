package cn.tongdun.kunpeng.api.acl.impl.kvcache.ignore;

import cn.tongdun.kunpeng.share.kv.Cursor;
import cn.tongdun.kunpeng.share.kv.IHashBinaryKVRepository;
import cn.tongdun.kunpeng.share.kv.IValue;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author yangchangkai
 * @date 2020/11/4
 */
@Component
public class IgnoreHashBinaryKVRepository implements IHashBinaryKVRepository {
    @Override
    public boolean hset(byte[] key, byte[] field, byte[] value) {
        return false;
    }

    @Override
    public byte[] hget(byte[] key, byte[] field) {
        return new byte[0];
    }

    @Override
    public boolean hdel(byte[] key, byte[]... field) {
        return false;
    }

    @Override
    public String hmset(byte[] key, Map<byte[], byte[]> hash) {
        return null;
    }

    @Override
    public String hmset(byte[] key, Map<byte[], byte[]> hash, long ttlInMilliSeconds) {
        return null;
    }

    @Override
    public List<byte[]> hmget(byte[] key, byte[]... fields) {
        return null;
    }

    @Override
    public List<IValue<byte[], byte[]>> hscan(byte[] prefix, Cursor cursor, int count) {
        return null;
    }

    @Override
    public Map<byte[], byte[]> hgetAll(byte[] key) {
        return null;
    }

    @Override
    public boolean exists(byte[] key) {
        return false;
    }

    @Override
    public boolean del(byte[] key) {
        return false;
    }

    @Override
    public byte[] get(byte[] key) {
        return new byte[0];
    }

    @Override
    public boolean set(byte[] key, byte[] value) {
        return false;
    }

    @Override
    public boolean set(byte[] key, byte[] value, long ttlInMilliSeconds) {
        return false;
    }

    @Override
    public long ttl(byte[] key) {
        return 0;
    }

    @Override
    public boolean setTtl(byte[] key, long ttlInMilliSeconds) {
        return false;
    }

    @Override
    public List<IValue<byte[], byte[]>> scan(byte[] prefix, Cursor cursor, int count) {
        return null;
    }

    @Override
    public void destroy() {

    }
}
