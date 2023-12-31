package cn.tongdun.kunpeng.api.infrastructure.redis;

import cn.fraudmetrix.common.client.redis.SimpleRedisClient;
import org.springframework.context.annotation.Bean;

/**
 * @Author: liang.chen
 * @Date: 2020/1/9 上午11:26
 */

//@Configuration
//@EnableCaching
public class RedisConfig{


    /**
     * 申明缓存管理器，会创建一个切面（aspect）并触发Spring缓存注解的切点（pointcut）
     * 根据类或者方法所使用的注解以及缓存的状态，这个切面会从缓存中获取数据，将数据添加到缓存之中或者从缓存中移除某个值

     * @return
     */
    @Bean
    public TdRedisCacheManager cacheManager(SimpleRedisClient kunPengRedisClient) {
        return TdRedisCacheManager.create(kunPengRedisClient);
    }

//    @Bean
//    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
//        // 创建一个模板类
//        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
//        // 将刚才的redis连接工厂设置到模板类中
//        template.setConnectionFactory(factory);
//        // 设置key的序列化器
//        template.setKeySerializer(new StringRedisSerializer());
//        // 设置value的序列化器
//        //使用Jackson 2，将对象序列化为JSON
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        //json转对象类，不设置默认的会将json转成hashmap
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//        template.setValueSerializer(jackson2JsonRedisSerializer);
//
//        return template;
//    }


}

