package cn.tongdun.kunpeng.api.acl.impl.kvcache.factory;

import io.codis.jodis.JedisResourcePool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Set;

/**
 * @author: yuanhang
 * @date: 2020-06-30 11:03
 **/
public class RedisSentinelPoolAdapter extends JedisSentinelPool implements JedisResourcePool {


    public RedisSentinelPoolAdapter(String masterName, Set<String> sentinels, GenericObjectPoolConfig poolConfig, int timeout, String password) {
        super(masterName, sentinels, poolConfig, timeout, password);
    }
}
