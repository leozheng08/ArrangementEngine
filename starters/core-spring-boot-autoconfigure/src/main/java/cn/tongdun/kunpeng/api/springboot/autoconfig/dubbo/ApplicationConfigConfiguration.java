package cn.tongdun.kunpeng.api.springboot.autoconfig.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ApplicationConfig
 *     <dubbo:application name="kunpeng-api" />
 * @author zhengwei
 * @date 2020/3/13 2:35 下午
 * @see ApplicationConfig
 **/
@Configuration("ApplicationConfigConfiguration4Api")
@ConditionalOnMissingBean(ApplicationConfig.class)
@ConditionalOnClass(ApplicationConfig.class)
@Slf4j
public class ApplicationConfigConfiguration implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ApplicationConfig.class);
        beanDefinition.getPropertyValues().add("name", "${app.name:kunpeng-api}");
        registry.registerBeanDefinition("applicationConfig", beanDefinition);

        log.info("Register ApplicationConfig success");
    }
}
