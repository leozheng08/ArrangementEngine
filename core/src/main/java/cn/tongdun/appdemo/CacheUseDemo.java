package cn.tongdun.appdemo;

import com.google.common.base.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import javax.annotation.PostConstruct;
import javax.cache.Cache;

public class CacheUseDemo {

    @Autowired
    private Cache<String, byte[]> redisCache;

    @PostConstruct
    public void init() {
        redisCache.put("key", "value".getBytes(Charsets.UTF_8));
    }

    @Cacheable(value = "spring-cache")
    public String getData() {
        return new String(redisCache.get("key"));
    }
}
