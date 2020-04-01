package cn.tongdun.kunpeng.api.springboot.autoconfig.mybatis;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import lombok.extern.slf4j.Slf4j;

/**
 * MapperScannerConfigurer
 *
 * @author zhengwei
 * @date 2020/3/13 1:52 下午
 * @see MapperScannerConfigurer
 **/
@Slf4j
@Configuration
@ConditionalOnMissingBean(name = "kunepgApiMapperScannerConfigurer")
public class MapperScannerConfigurerConfiguration implements ImportBeanDefinitionRegistrar {

//    <bean name="kunepgApiMapperScannerConfigurer" class="org.mybatis.spring.mapper.MapperScannerConfigurer">
//        <property name="basePackage" value="cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng"/>
//        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
//    </bean>
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperScannerConfigurer.class);
        beanDefinition.getPropertyValues().addPropertyValue("basePackage", "${mybatis.mapper.basePackage:cn.tongdun.kunpeng.admin.infrastructure.persistence.mapper}");
        beanDefinition.getPropertyValues().addPropertyValue("sqlSessionFactoryBeanName", "kunpengApiSqlSessionFactory");
        registry.registerBeanDefinition("kunepgApiMapperScannerConfigurer", beanDefinition);

        log.info("Register MapperScannerConfigurer success");

    }
}
