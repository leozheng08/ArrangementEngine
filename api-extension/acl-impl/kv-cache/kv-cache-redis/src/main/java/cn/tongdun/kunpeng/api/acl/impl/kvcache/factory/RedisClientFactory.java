package cn.tongdun.kunpeng.api.acl.impl.kvcache.factory;

import cn.fraudmetrix.common.client.redis.RedisClient;
import cn.fraudmetrix.common.client.redis.SimpleRedisClient;
import cn.fraudmetrix.common.client.redis.factory.RoundRobinStandalonePool;
import io.codis.jodis.JedisResourcePool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author: yuanhang
 * @date: 2020-06-29 18:00
 **/
public class RedisClientFactory implements InitializingBean, DisposableBean, FactoryBean<RedisClient> {

    Environment environment;

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

    /**
     * 在AbstractRedisFactory的基础上再封装一层来适配不同的环境
     */

    @Override
    public void afterPropertiesSet() throws Exception {
        if ("staging".equals(environment.getActiveProfiles()) || "prod".equals(environment.getActiveProfiles())) {
            this.pool = RoundRobinStandalonePool.create().servers(servers.split(",")).connectionTimeoutMs(connectionTimeOut).soTimeoutMs(socketTimeOut).password(this.getPassword()).poolConfig(new JedisPoolConfig()).build();
        }
        this.pool = new RedisSentinelPoolAdapter(this.getMasterName(), new HashSet(Arrays.asList(sentinels)), new JedisPoolConfig(), 3000, this.getPassword());
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
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
    public void destroy() throws Exception {

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
