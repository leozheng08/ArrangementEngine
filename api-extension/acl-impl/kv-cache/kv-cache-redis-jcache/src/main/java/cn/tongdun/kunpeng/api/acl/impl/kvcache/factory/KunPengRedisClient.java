package cn.tongdun.kunpeng.api.acl.impl.kvcache.factory;

import cn.fraudmetrix.cache.redis.RedisClient;
import cn.fraudmetrix.cache.redis.jedis.JedisPoolFactory;
import cn.fraudmetrix.cache.redis.jedis.JedisResourcePool;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.net.URI;
import java.util.Properties;

/**
 * @author: pei.liu
 * 使用JCache创建RedisClient
 **/
@Component
public class KunPengRedisClient implements InitializingBean, FactoryBean<RedisClient> {

    @Value("${spring.cache.cache-names:}")
    String cacheName;

    /**
     * "redis://masterName:password@hostname:port/database?queryString"

     */
    @Value("${spring.cache.jcache.config:}")
    String url;

    JedisResourcePool pool;


    @Override
    public void afterPropertiesSet() throws Exception {
        //直接初始化底层Jedis Pool，自行管理Jedis生命周期，自行选择处理key的前缀
        javax.cache.configuration.Factory<JedisResourcePool> factory = JedisPoolFactory.createJedisPoolFactory(URI.create(url), new Properties());
        // 此处请管理JedisResourcePool的生命周期。
        this.pool = factory.create();
    }

    @Override
    public RedisClient getObject() throws Exception {
        return new RedisClient(cacheName, pool::getResource, Jedis::close);
    }

    @Override
    public Class<?> getObjectType() {
        return RedisClient.class;
    }

}
