package cn.tongdun.kunpeng.api.engine.reload.reload;

import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 已成功刷新事件列表
 * @Author: liang.chen
 * @Date: 2020/3/6 下午3:26
 */
@Component
public class FinishEventCache {

    //entityName+uuid+version(即时间戳) -> Policy
    private Cache<String, String> cache;


    @PostConstruct
    public void init(){
        //设置缓存有效期
        CacheBuilder<String, String> cacheBuilder = (CacheBuilder) CacheBuilder.newBuilder();
        cacheBuilder.expireAfterWrite(10, TimeUnit.MINUTES);
        cache = cacheBuilder.build();
    }


    public String get(String key){
        return cache.getIfPresent(key);
    }


    public void put(String key, String event){
        cache.put(key,event);
    }

}
