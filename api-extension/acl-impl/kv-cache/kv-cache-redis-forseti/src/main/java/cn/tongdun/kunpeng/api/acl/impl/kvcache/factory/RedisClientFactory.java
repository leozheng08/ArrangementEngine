package cn.tongdun.kunpeng.api.acl.impl.kvcache.factory;

import cn.fraudmetrix.common.client.redis.RedisClient;
import cn.fraudmetrix.common.client.redis.SimpleRedisClient;
import cn.tongdun.kunpeng.api.common.config.ILocalEnvironment;
import io.codis.jodis.JedisResourcePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author: yuanhang
 * @date: 2020-06-29 18:00
 **/
public class RedisClientFactory implements InitializingBean, FactoryBean<RedisClient> {

    private static Logger logger = LoggerFactory.getLogger(RedisClientFactory.class);

    ILocalEnvironment environment;

    String masterName;

    String sentinels;

    String namespace;

    String clientName;

    String servers;

    String password;

    String monitorCenter;

    JedisResourcePool pool;

    private Integer connectionTimeOut = 3000;

    private Integer socketTimeOut = 3000;


    @Override
    public void afterPropertiesSet() throws Exception {
        this.pool = new RedisSentinelPoolAdapter(this.getMasterName(), new HashSet(Arrays.asList(sentinels)), new JedisPoolConfig(), socketTimeOut, this.getPassword());
    }

    public ILocalEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(ILocalEnvironment environment) {
        this.environment = environment;
    }

    public JedisResourcePool getPool() {
        return pool;
    }

    public void setPool(JedisResourcePool pool) {
        this.pool = pool;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getSentinels() {
        return sentinels;
    }

    public void setSentinels(String sentinels) {
        this.sentinels = sentinels;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

    @Override
    public RedisClient getObject() throws Exception {
        return new SimpleRedisClient(this.namespace, this.pool);
    }

    @Override
    public Class<?> getObjectType() {
        return SimpleRedisClient.class;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMonitorCenter() {
        return monitorCenter;
    }

    public void setMonitorCenter(String monitorCenter) {
        this.monitorCenter = monitorCenter;
    }
}
