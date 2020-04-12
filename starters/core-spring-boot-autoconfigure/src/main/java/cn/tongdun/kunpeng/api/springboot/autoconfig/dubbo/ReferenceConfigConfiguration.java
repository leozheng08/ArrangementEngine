package cn.tongdun.kunpeng.api.springboot.autoconfig.dubbo;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * RegistryConfig
 * <dubbo:registry group="${dubbo.zookeeper.root}" protocol="zookeeper" address="${dubbo.zookeeper.host}" port="${dubbo.port}" />
 * @author zhengwei
 * @date 2020/3/13 2:37 下午
 * @see RegistryConfig
 **/
@Configuration("ReferenceConfigConfiguration4Api")
@ConditionalOnMissingBean(name = "referenceConfig")
@Slf4j
public class ReferenceConfigConfiguration implements ImportBeanDefinitionRegistrar {

//    <!--用于dubbo泛化调用-->
//    <bean id="referenceConfig" class="com.alibaba.dubbo.config.ReferenceConfig" autowire="byType">
//        <property name="application">
//            <bean class="com.alibaba.dubbo.config.ApplicationConfig" autowire="byType">
//                <property name="name" value="forseti-api-generic"/>
//                <property name="registry">
//                    <bean class="com.alibaba.dubbo.config.RegistryConfig" autowire="byType">
//                        <property name="address" value="${dubbo.zookeeper.generic}"/>
//                        <property name="protocol" value="zookeeper"/>
//                        <property name="group" value="${dubbo.zookeeper.root}"/>
//                    </bean>
//                </property>
//            </bean>
//        </property>
//        <property name="generic" value="true"/>
//    </bean>
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ReferenceConfig.class);
        beanDefinition.getPropertyValues().add("application", "applicationConfig");
        beanDefinition.getPropertyValues().add("generic", "true");
        registry.registerBeanDefinition("referenceConfig", beanDefinition);

        log.info("Register RegistryConfig success");
    }
}
