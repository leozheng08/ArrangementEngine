package cn.tongdun.kunpeng.api.infrastructure.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @Author: liang.chen
 * @Date: 2020/2/25 下午3:38
 */
@Configuration
public class DynamicConfigInit {
    //资源信息元数据:PropertySource包含name和泛型，一份资源信息存在唯一的name以及对应泛型数据，在这里设计为泛型表明可拓展自定.PropertySource在集合中的唯一性只能去看name
    public static final String DYNAMIC_CONFIG_NAME = "dynamic_config";
    @Autowired
    AbstractEnvironment environment;

    @Value("${configmap.path}")
    private String configmapPath;

//    @Autowired
    private DynamicLoadPropertySource propertySource;


//    @Bean(name = "dynamicLoadPropertySource")
//    public DynamicLoadPropertySource createDynamicLoadPropertySource(){
//        return new DynamicLoadPropertySource(DYNAMIC_CONFIG_NAME,configmapPath+"/app.properties");
//    }

    @PostConstruct
    public void init() {
        propertySource = new DynamicLoadPropertySource(DYNAMIC_CONFIG_NAME,configmapPath+"/app.properties");
        environment.getPropertySources().addFirst(propertySource);
    }

    public DynamicLoadPropertySource getPropertySource() {
        return propertySource;
    }

    //    @Scheduled(cron = "*/10 * * * * ?")
//    public void scheduling() {
//        propertySource.refresh();
//    }
}