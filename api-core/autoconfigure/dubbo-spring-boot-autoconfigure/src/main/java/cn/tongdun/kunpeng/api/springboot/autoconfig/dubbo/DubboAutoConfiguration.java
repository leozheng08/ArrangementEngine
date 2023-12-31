package cn.tongdun.kunpeng.api.springboot.autoconfig.dubbo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * dubbo
 *
 * @author zhengwei
 * @date 2020/3/13 2:21 下午
 **/
@Configuration("DubboAutoConfiguration4Api")
@Import({ApplicationConfigConfiguration.class, RegistryConfigConfiguration.class,ReferenceConfigConfiguration.class})
public class DubboAutoConfiguration {

}
