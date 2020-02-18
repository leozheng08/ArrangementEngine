package cn.tongdun.kunpeng.api.engine.cache;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: liuq
 * @Date: 2020/2/18 1:11 PM
 */
public abstract class AbstractBatchLocalCache<K,V> implements IBatchLocalCache<K,V>{

    @Autowired
    LocalCacheService localCacheService;

    public <V> void register(Class<V> clazz){
        localCacheService.register(clazz,this);
    }
}
