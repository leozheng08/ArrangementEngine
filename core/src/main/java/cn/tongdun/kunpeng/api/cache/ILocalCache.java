package cn.tongdun.kunpeng.api.cache;

import cn.tongdun.kunpeng.api.core.rule.Rule;

/**
 * 本地缓存
 * @Author: liang.chen
 * @Date: 2019/12/20 下午3:34
 */
public interface ILocalCache<K,V> {

    V get(K key);

    void put(K key,V value);

    V remove(K uuid);
}
