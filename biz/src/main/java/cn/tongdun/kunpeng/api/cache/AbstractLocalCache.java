package cn.tongdun.kunpeng.api.cache;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;

/**
 * 本地缓存
 * @Author: liang.chen
 * @Date: 2019/12/20 下午3:34
 */
public abstract class AbstractLocalCache<K,V> implements ILocalCache<K,V>{

    @Autowired
    LocalCacheService localCacheService;

    public <V> void register(Class<V> clazz){
        localCacheService.register(clazz,this);
    }

}
