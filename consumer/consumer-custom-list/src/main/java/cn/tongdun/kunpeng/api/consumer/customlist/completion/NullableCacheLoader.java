package cn.tongdun.kunpeng.api.consumer.customlist.completion;

import com.google.common.cache.CacheLoader;

public class NullableCacheLoader<K, V> extends CacheLoader<K, V> {

    public static final Object NULL_VALUE = new Object();

    private CacheLoader<K, V>  loader;

    public NullableCacheLoader(CacheLoader<K, V> loader){
        this.loader = loader;
    }

    @Override
    public V load(K k) throws Exception {
        V result = loader.load(k);
        if (result == null) {
            return (V) NULL_VALUE;
        }
        return result;
    }
}
