package cn.tongdun.kunpeng.api.acl.impl.kvcache.redis.autoconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 装配kv-redis组件
 *
 * @Author: liang.chen
 * @Date: 2020/4/7 下午6:42
 **/
@Configuration
@Import(RedisConfiguration.class)
public class RedisAutoConfiguration {

}
