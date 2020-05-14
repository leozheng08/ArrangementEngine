package cn.tongdun.kunpeng.api.springboot.autoconfig.kafka;

import cn.fraudmetrix.module.kafka.util.ZKConfigCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Author: liang.chen
 * @Date: 2020/4/21 下午7:04
 */
@Configuration("zkConfigCenterConfiguration")
@ConditionalOnMissingBean(name = "zkConfigCenter")
@Slf4j
public class ZkConfigCenterConfiguration implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

//    <bean id="zkConfigCenter" class="cn.fraudmetrix.module.kafka.util.ZKConfigCenter" init-method="init" destroy-method="close">
//        <property name="zkserver" value="${configcenter.endpoint}"/>
//        <property name="businessUnit" value="${business.unit}"/>
//    </bean>

        RootBeanDefinition beanDefinition = new RootBeanDefinition(ZKConfigCenter.class);
        beanDefinition.setInitMethodName("init");
        beanDefinition.setDestroyMethodName("close");
        beanDefinition.getPropertyValues().add("zkserver", "${configcenter.endpoint}");
        beanDefinition.getPropertyValues().add("businessUnit", "${business.unit}");
        registry.registerBeanDefinition("zkConfigCenter", beanDefinition);

        log.info("kunpeng api Register ZKConfigCenter success");
    }
}
