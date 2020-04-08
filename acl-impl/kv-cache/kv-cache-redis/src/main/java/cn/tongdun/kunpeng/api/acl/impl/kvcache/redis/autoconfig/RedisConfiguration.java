package cn.tongdun.kunpeng.api.acl.impl.kvcache.redis.autoconfig;

import cn.tongdun.kunpeng.share.config.DynamicConfigInit;
import cn.tongdun.kunpeng.share.config.DynamicConfigRepository;
import cn.tongdun.kunpeng.share.config.IConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 注册一个动态配置实现
 *
 * <p>
 * 默认为config map实现，默认路径：/home/admin/configmap/app.properties
 *
 * @author zhengwei
 * @date 2020/3/6 1:18 下午
 * @see DynamicConfigInit
 * @see DynamicConfigRepository
 **/
@Configuration
@ConditionalOnMissingBean()
@ConditionalOnClass(DynamicConfigRepository.class)
//@ConditionalOnProperty(name = "kunpeng.dynamic.config.type", havingValue = "configmap")
@Slf4j
public class RedisConfiguration implements ImportBeanDefinitionRegistrar {

//    <redis:sentinel id="kunPengRedisClient" master-name="${kunpeng-api.redis.master}"
//    sentinels="${kunpeng-api.redis.sentinels}"
//    namespace="${kunpeng-api.redis.namespace}" password="${kunpeng-api.redis.password}"
//    monitor-center="${kunpeng-api.redis.monitor.center}"  />

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition dynamicConfigInit = new RootBeanDefinition(DynamicConfigInit.class);
        registry.registerBeanDefinition("dynamicConfigInit", dynamicConfigInit);

        RootBeanDefinition configBeanDefinition = new RootBeanDefinition(DynamicConfigRepository.class);
        registry.registerBeanDefinition("dynamicConfigRepository", configBeanDefinition);

        log.info("Register DynamicConfigRepository success");
    }
}
