package cn.tongdun.kunpeng.api.acl.impl.kvcache.ignore;


import cn.tongdun.kunpeng.share.kv.Cursor;
import cn.tongdun.kunpeng.share.kv.IKVRepository;
import cn.tongdun.kunpeng.share.kv.IValue;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lvyadong on 2020/02/20.
 */
@Component
public class IgnoreKVRepository implements IKVRepository {
    private static final List EMPTY_LIST = new ArrayList(0);

    @Override
    public boolean exists(String key) {
        return false;
    }

    @Override
    public boolean del(String key) {
        return false;
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public boolean set(String key, String value) {
        return false;
    }

    @Override
    public boolean set(String key, String value, long ttlInMilliSeconds) {
        return false;
    }

    @Override
    public long ttl(String key) {
        return 0;
    }

    @Override
    public boolean setTtl(String key, long ttlInMilliSeconds) {
        return false;
    }

    @Override
    public List<IValue<String>> scan(String prefix, Cursor cursor, int count) {
        return EMPTY_LIST;
    }

    @Override
    public void destroy() {
    }
}

