package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * 默认的GenericServiceManager
 * <p>
 * 根据接口信息缓存泛化调用ReferenceConfig
 *
 * @author zhengwei
 * @date 2019-08-09 16:50
 **/
@Service
public class DefaultGenericServiceManager implements GenericServiceManager {
    private static final Logger logger = LoggerFactory.getLogger(DefaultGenericServiceManager.class);

    /**
     * 由于决策流没有时间限制,防止配置接口时候设置了一个太大的超时时间造成规则引擎执行资源耗尽,故将超时时间设置为kunta接口的最长时间
     */
    private final static int KUNTA_MAX_EXECUTE_TIME = 20000;

    /**
     * 核心线程数
     */
    private static final String DEFAULT_GENERIC_CORE_THREADS = "2";

    /**
     * 最大线程数
     */
    private static final String DEFAULT_GENERIC_THREADS = "8";

    /**
     * 缓存的最大数量
     */
    @Value("${generic.interface.cache.count:128}")
    private int cacheMaxCount = 128;

    /**
     * 缓存并发等级
     */
    @Value("${generic.interface.cache.concurrencyLevel:-1}")
    private int concurrencyLevel = -1;

    /**
     * parent ReferenceConfig 设置了通用的属性
     */
    @Resource(name = "referenceConfig")
    private ReferenceConfig<GenericService> parentReferenceConfig;

    /**
     * ConsumerConfig
     */
    @Autowired
    private ConsumerConfig consumerConfig;


    /**
     * key={@link #buildKey(InterfaceDefinition)}, value=ReferenceConfig
     */
    private Cache<String, ReferenceConfig<GenericService>> referenceConfigCache;

    @PostConstruct
    public void init() {
        CacheBuilder cacheBuilder = CacheBuilder.newBuilder().maximumSize(this.cacheMaxCount);
        if (this.concurrencyLevel <= 0) {
            cacheBuilder.concurrencyLevel(Runtime.getRuntime().availableProcessors());
        } else {
            cacheBuilder.concurrencyLevel(this.concurrencyLevel);
        }
        this.referenceConfigCache = cacheBuilder.build();
    }

    @PreDestroy
    public void destroy() {
        if (this.referenceConfigCache != null && this.referenceConfigCache.size() > 0) {
            ConcurrentMap<String, ReferenceConfig<GenericService>> referenceConfigConcurrentMap = this.referenceConfigCache.asMap();
            Collection<ReferenceConfig<GenericService>> referenceConfigs = referenceConfigConcurrentMap.values();
            for (ReferenceConfig<GenericService> config : referenceConfigs) {
                config.destroy();
            }
        }
    }

    @Override
    public ReferenceConfig<GenericService> getGenericServiceReferenceConfig(final InterfaceDefinition interfaceDefinition) {
        final String key = buildKey(interfaceDefinition);

        try {
            return getReferenceConfigFromCache(key, interfaceDefinition);
        } catch (ExecutionException e) {
            logger.error("getReferenceConfigFromCache error, key={}, interfaceDO={}", key, interfaceDefinition, e);
        }

        // 正常情况不会走到这里
        ReferenceConfig<GenericService> referenceConfig = createReferenceConfig(key, interfaceDefinition);
        this.referenceConfigCache.put(key, referenceConfig);
        return referenceConfig;
    }

    /***
     * 创建dubbo调用的reference config
     *
     * @param key 缓存key
     * @param interfaceDefinition 接口信息
     * @return ReferenceConfig
     */
    private ReferenceConfig<GenericService> createReferenceConfig(final String key, final InterfaceDefinition interfaceDefinition) {
        logger.info("create ReferenceConfig, key={}, interfaceDO={}", key, interfaceDefinition);
        ReferenceConfig<GenericService> newReferenceConfig = new ReferenceConfig<>();
        newReferenceConfig.setApplication(parentReferenceConfig.getApplication());
        newReferenceConfig.setConsumer(consumerConfig);
        newReferenceConfig.setInterface(interfaceDefinition.getServiceName());
        newReferenceConfig.setVersion(interfaceDefinition.getVersion());
        newReferenceConfig.setTimeout(Math.min(interfaceDefinition.getTimeout(), KUNTA_MAX_EXECUTE_TIME));
        newReferenceConfig.setConnections(1);
        newReferenceConfig.setGeneric(true);
        newReferenceConfig.setRetries(interfaceDefinition.getRetryCount());

        Map<String, String> parameters = Maps.newHashMapWithExpectedSize(5);
        parameters.put("threadname", buildThreadNamePrefix(interfaceDefinition));
        parameters.put("threadpool", "cached");
        parameters.put("corethreads", DEFAULT_GENERIC_CORE_THREADS);
        parameters.put("threads", DEFAULT_GENERIC_THREADS);
        parameters.put("default.reference.filter", "seqid");
        newReferenceConfig.setParameters(parameters);

        return newReferenceConfig;
    }

    private String buildThreadNamePrefix(InterfaceDefinition interfaceDefinition) {
        StringBuilder keyBuilder = new StringBuilder("Dubbo-generic-");
        keyBuilder.append(getServiceSimpleName(interfaceDefinition.getServiceName())).append(":").append(interfaceDefinition.getTimeout())
                .append(":").append(interfaceDefinition.getVersion()).append(":").append(interfaceDefinition.getRetryCount());
        return keyBuilder.toString();
    }

    /**
     * 服务简称（类名）
     *
     * @param serviceName 全称，包含包名
     * @return 简称
     */
    private String getServiceSimpleName(String serviceName) {
        return serviceName.substring(serviceName.lastIndexOf(".") + 1);
    }

    /**
     * 从缓存中获取
     *
     * @param key         缓存key
     * @param interfaceDefinition 接口信息
     * @return ReferenceConfig
     * @throws ExecutionException createReferenceConfig方法出错
     */
    private ReferenceConfig<GenericService> getReferenceConfigFromCache(final String key, final InterfaceDefinition interfaceDefinition) throws ExecutionException {
        // 参数肯定不为空或者null，那么这里就不判断了
        return this.referenceConfigCache.get(key, new Callable<ReferenceConfig<GenericService>>() {
            @Override
            public ReferenceConfig<GenericService> call() throws Exception {
                return createReferenceConfig(key, interfaceDefinition);
            }
        });
    }

    /**
     * build key
     * <p>
     * service_name + ":" + timeout + ":" + version + ":" + retry
     *
     * @param interfaceDefinition 接口信息
     * @return key service_name + ":" + timeout + ":" + version + ":" + retry
     */
    private String buildKey(InterfaceDefinition interfaceDefinition) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(interfaceDefinition.getServiceName()).append(":").append(interfaceDefinition.getTimeout())
                .append(":").append(interfaceDefinition.getVersion()).append(":").append(interfaceDefinition.getRetryCount());
        return keyBuilder.toString();
    }

    @Override
    public GenericService getGenericService(final InterfaceDefinition interfaceDefinition) {
        // 参数肯定不为空或者null，那么这里就不判断了
        return this.getGenericServiceReferenceConfig(interfaceDefinition).get();
    }

    public ReferenceConfig<GenericService> getParentReferenceConfig() {
        return parentReferenceConfig;
    }

    public void setParentReferenceConfig(ReferenceConfig<GenericService> parentReferenceConfig) {
        this.parentReferenceConfig = parentReferenceConfig;
    }

    public ConsumerConfig getConsumerConfig() {
        return consumerConfig;
    }

    public void setConsumerConfig(ConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

}
