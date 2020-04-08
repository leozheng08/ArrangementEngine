package cn.tongdun.kunpeng.api.springboot.autoconfig.jdbc;

import com.alibaba.druid.pool.DruidDataSource;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * DruidDataSource
 *
 * @author zhengwei
 * @date 2020/3/13 3:19 下午
 *
 * @see DruidDataSource
 **/
@Configuration("DruidDataSourceConfiguration4Api")
@ConditionalOnMissingBean(name = "kunpengApiDataSource")
@Slf4j
public class DruidDataSourceConfiguration implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

//<bean id="kunpengApiDataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init"
//        destroy-method="close">
//        <property name="driverClassName" value="${jdbc.kunpeng.database.driver}"/>
//        <property name="url" value="${jdbc.kunpeng.database.url}&amp;allowMultiQueries=true"/>
//        <property name="username" value="${jdbc.kunpeng.database.username}"/>
//        <property name="password" value="${jdbc.kunpeng.database.password}"/>
//        <property name="defaultAutoCommit" value="false"/>
//
//        <!-- 配置初始化大小、最小、最大 -->
//        <property name="initialSize" value="1"/>
//        <property name="minIdle" value="1"/>
//        <property name="maxActive" value="10"/>
//
//        <!-- 配置获取连接等待超时的时间 -->
//        <property name="maxWait" value="60000"/>
//    </bean>

        RootBeanDefinition beanDefinition = new RootBeanDefinition(DruidDataSource.class);
        beanDefinition.setInitMethodName("init");
        beanDefinition.setDestroyMethodName("close");
        beanDefinition.getPropertyValues().add("driverClassName", "${jdbc.kunpeng.database.driver}");
        beanDefinition.getPropertyValues().add("url", "${jdbc.kunpeng.database.url}");
        beanDefinition.getPropertyValues().add("username", "${jdbc.kunpeng.database.username}");
        beanDefinition.getPropertyValues().add("password", "${jdbc.kunpeng.database.password}");
        beanDefinition.getPropertyValues().add("defaultAutoCommit", "${jdbc.kunpeng.database.defaultAutoCommit:false}");
        beanDefinition.getPropertyValues().add("initialSize", "${jdbc.kunpeng.database.initialSize:1}");
        beanDefinition.getPropertyValues().add("minIdle", "${jdbc.kunpeng.database.minIdle:1}");
        beanDefinition.getPropertyValues().add("maxActive", "${jdbc.kunpeng.database.maxActive:10}");
        beanDefinition.getPropertyValues().add("maxWait", "${jdbc.kunpeng.database.maxWait:60000}");
        registry.registerBeanDefinition("kunpengApiDataSource", beanDefinition);

        log.info("Register DruidDataSource success");
    }
}
