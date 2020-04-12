package cn.tongdun.kunpeng.api.springboot.autoconfig.jdbc;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhengwei
 * @date 2020/3/13 3:18 下午
 **/
@Configuration("DruidDataSourceAutoConfiguration4Api")
@Import(DruidDataSourceConfiguration.class)
public class DruidDataSourceAutoConfiguration {
}
