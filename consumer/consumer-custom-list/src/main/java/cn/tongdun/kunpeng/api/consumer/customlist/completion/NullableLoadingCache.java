package cn.tongdun.kunpeng.api.consumer.customlist.completion;

import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

public class NullableLoadingCache<K, V> implements LoadingCache<K, V> {

    private LoadingCache<K, V> cache;

    public NullableLoadingCache(LoadingCache<K, V> cache){
        this.cache = cache;
    }

    @Override
    public V get(K k) throws ExecutionException {
        V result = cache.get(k);
        if (result == NullableCacheLoader.NULL_VALUE) {
            return null;
        }
        return result;
    }

    @Override
    public V getUnchecked(K k) {
        return cache.getUnchecked(k);
    }

    @Override
    public ImmutableMap<K, V> getAll(Iterable<? extends K> iterable) throws ExecutionException {
        return cache.getAll(iterable);
    }

    @Override
    public V apply(K k) {
        return cache.apply(k);
    }

    @Override
    public void refresh(K k) {
        cache.refresh(k);
    }

    @Override
    public V getIfPresent(Object o) {
        return cache.getIfPresent(o);
    }

    @Override
    public V get(K k, Callable<? extends V> callable) throws ExecutionException {
        return cache.get(k, callable);
    }

    @Override
    public ImmutableMap<K, V> getAllPresent(Iterable<?> iterable) {
        return cache.getAllPresent(iterable);
    }

    @Override
    public void put(K k, V v) {
        cache.put(k, v);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        cache.putAll(map);
    }

    @Override
    public void invalidate(Object o) {
        cache.invalidate(o);
    }

    @Override
    public void invalidateAll(Iterable<?> iterable) {
        cache.invalidateAll(iterable);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

    @Override
    public long size() {
        return cache.size();
    }

    @Override
    public CacheStats stats() {
        return cache.stats();
    }

    @Override
    public ConcurrentMap<K, V> asMap() {
        return cache.asMap();
    }

    @Override
    public void cleanUp() {
        cache.cleanUp();
    }
}
