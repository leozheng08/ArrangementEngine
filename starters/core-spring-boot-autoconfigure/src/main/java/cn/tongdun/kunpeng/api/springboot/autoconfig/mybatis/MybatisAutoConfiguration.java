package cn.tongdun.kunpeng.api.springboot.autoconfig.mybatis;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * mybatis
 *
 * @author zhengwei
 * @date 2020/3/13 1:44 下午
 **/
@Import({MybatisSqlSessionFactoryConfiguration.class, MybatisSqlSessionTemplateConfiguration.class, MapperScannerConfigurerConfiguration.class})
@Configuration("MybatisAutoConfiguration4Api")
public class MybatisAutoConfiguration {

}
