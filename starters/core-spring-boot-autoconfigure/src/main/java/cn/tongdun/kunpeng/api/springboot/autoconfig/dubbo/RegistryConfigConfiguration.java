package cn.tongdun.kunpeng.api.springboot.autoconfig.dubbo;

import com.alibaba.dubbo.config.RegistryConfig;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import lombok.extern.slf4j.Slf4j;

/**
 * RegistryConfig
 * <dubbo:registry group="${dubbo.zookeeper.root}" protocol="zookeeper" address="${dubbo.zookeeper.host}" port="${dubbo.port}" />
 * @author zhengwei
 * @date 2020/3/13 2:37 下午
 * @see RegistryConfig
 **/
@Configuration("RegistryConfigConfiguration4Api")
@ConditionalOnMissingBean(RegistryConfig.class)
@ConditionalOnClass(RegistryConfig.class)
@Slf4j
public class RegistryConfigConfiguration implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(RegistryConfig.class);
        beanDefinition.getPropertyValues().add("group", "${dubbo.zookeeper.root}");
        beanDefinition.getPropertyValues().add("protocol", "${dubbo.registry.protocol:zookeeper}");
        beanDefinition.getPropertyValues().add("address", "${dubbo.zookeeper.host}");
        beanDefinition.getPropertyValues().add("client", "${dubbo.registry.client:curator}");
        registry.registerBeanDefinition("registryConfig", beanDefinition);

        log.info("Register RegistryConfig success");
    }
}
