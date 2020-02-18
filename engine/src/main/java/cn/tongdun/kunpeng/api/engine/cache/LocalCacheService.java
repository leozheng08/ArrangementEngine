package cn.tongdun.kunpeng.api.engine.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2019/12/20 下午3:46
 */
@Component
public class LocalCacheService {
    //保存各个类对应的本地缓存
    Map<Class, ILocalCache> cacheMap = new HashMap<>(5);
    Map<Class, IBatchLocalCache> batchCacheMap = new HashMap<>(2);

    public void register(Class clazz, ILocalCache localCache) {
        cacheMap.put(clazz, localCache);
    }

    public ILocalCache getLocalCache(Class clazz) {
        return cacheMap.get(clazz);
    }

    public void register(Class clazz, IBatchLocalCache batchLocalCache) {
        batchCacheMap.put(clazz, batchLocalCache);
    }

    public IBatchLocalCache getBatchLocalCache(Class clazz) {
        return batchCacheMap.get(clazz);
    }


    public <K, V> V get(Class<V> vClass, K key) {
        ILocalCache<K, V> cache = getLocalCache(vClass);
        if (cache == null) {
            return null;
        }
        return cache.get(key);
    }

    public <V> void put(Class<V> vClass, Object key, V value) {
        ILocalCache<Object, V> cache = getLocalCache(vClass);
        if (cache == null) {
            return;
        }
        cache.put(key, value);
    }

    public <V> void putList(Class<V> vClass, Object key, List<V> value) {
        IBatchLocalCache<Object, V> cache = getBatchLocalCache(vClass);
        if (cache == null) {
            return;
        }
        cache.putList(key, value);
    }

    public <K, V> List<V> getList(Class<V> vClass, K key) {
        IBatchLocalCache<K, V> cache = getBatchLocalCache(vClass);
        if (cache == null) {
            return null;
        }
        return cache.getList(key);
    }

}
