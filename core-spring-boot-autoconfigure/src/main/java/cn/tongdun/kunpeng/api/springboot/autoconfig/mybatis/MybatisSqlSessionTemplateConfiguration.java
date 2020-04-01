package cn.tongdun.kunpeng.api.springboot.autoconfig.mybatis;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import lombok.extern.slf4j.Slf4j;

/**
 * mybatis
 *
 * @author zhengwei
 * @date 2020/3/13 1:47 下午
 *
 * @see SqlSessionTemplate
 **/
@Slf4j
@Configuration
@ConditionalOnMissingBean(name = "")
public class MybatisSqlSessionTemplateConfiguration implements ImportBeanDefinitionRegistrar {

//    <bean id="kunpengApiSqlSessionTemplate" class="org.mybatis.spring.SqlSessionTemplate">
//        <constructor-arg ref="sqlSessionFactory"/>
//    </bean>
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(SqlSessionTemplate.class);
        beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, new RuntimeBeanReference("kunpengApiSqlSessionFactory"));
        registry.registerBeanDefinition("kunpengApiSqlSessionTemplate", beanDefinition);

        log.info("Register kunpengApiSqlSessionTemplate success");
    }
}
