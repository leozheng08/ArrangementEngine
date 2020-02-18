package cn.tongdun.kunpeng.api.engine.cache;

import java.util.List;

/**
 * @Author: liuq
 * @Date: 2020/2/18 1:06 PM
 */
public interface IBatchLocalCache<K, V> {

    void putList(K key, List<V> value);

    List<V> getList(K key);

    List<V> removeList(K uuid);
}
