package cn.tongdun.kunpeng.api.engine.reload;

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

    //entityName+uuid+version(即时间戳) -> true
    private Cache<String, Boolean> cache;


    @PostConstruct
    public void init(){
        //设置缓存有效期10分钟
        CacheBuilder<String, Boolean> cacheBuilder = (CacheBuilder) CacheBuilder.newBuilder();
        cacheBuilder.expireAfterWrite(6, TimeUnit.MINUTES);
        cache = cacheBuilder.build();
    }


    public Boolean get(String key){
        return cache.getIfPresent(key);
    }


    public void put(String key, Boolean event){
        cache.put(key,event);
    }


    public boolean contains(String key){
        Boolean value = get(key);
        if(value == null){
            return false;
        }
        return true;
    }

}
