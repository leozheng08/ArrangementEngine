package cn.tongdun.kunpeng.api.engine.cache;

import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

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
