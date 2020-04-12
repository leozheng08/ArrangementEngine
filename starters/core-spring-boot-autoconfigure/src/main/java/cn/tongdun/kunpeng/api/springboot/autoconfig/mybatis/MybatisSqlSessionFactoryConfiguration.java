package cn.tongdun.kunpeng.api.springboot.autoconfig.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import lombok.extern.slf4j.Slf4j;

/**
 * mybatis SqlSessionFactory
 *
 * @author zhengwei
 * @date 2020/3/6 6:27 下午
 * @see SqlSessionFactory
 **/
@Slf4j
@Configuration("MybatisSqlSessionFactoryConfiguration4Api")
@ConditionalOnMissingBean(name = "kunpengApiSqlSessionFactory")
public class MybatisSqlSessionFactoryConfiguration implements ImportBeanDefinitionRegistrar {

//    <bean id="kunpengApiSqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
//        <property name="configLocation" value="classpath:mybatis/mysql-sqlmap-config.xml"/>
//        <property name="mapperLocations" value="classpath:mybatis/mappers/kunpeng/*.xml"/>
//        <property name="typeAliasesPackage" value="cn.tongdun.kunpeng.share.dataobject"/>
//        <property name="dataSource" ref="kunpengApiDataSource"/>
//    </bean>
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(SqlSessionFactoryBean.class);
        beanDefinition.getPropertyValues().add("configLocation", "${kunpeng.api.mybatis.configLocation:classpath:mybatis/mysql-sqlmap-config.xml}");
        beanDefinition.getPropertyValues().add("mapperLocations", "${kunpeng.api.mybatis.mapperLocations:classpath*:mybatis/mappers/kunpeng/*.xml}");
        beanDefinition.getPropertyValues().add("typeAliasesPackage", "${kunpeng.api.mybatis.typeAliasesPackage:cn.tongdun.kunpeng.share.dataobject}");
        beanDefinition.getPropertyValues().add("dataSource", new RuntimeBeanReference("kunpengApiDataSource"));

        registry.registerBeanDefinition("kunpengApiSqlSessionFactory", beanDefinition);

        log.info("Register kunpengApiSqlSessionFactory success");
    }
}
