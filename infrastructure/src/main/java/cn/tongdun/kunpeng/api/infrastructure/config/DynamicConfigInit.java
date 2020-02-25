package cn.tongdun.kunpeng.api.infrastructure.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 动态配置类DynamicLoadPropertySource初始化
 * @Author: liang.chen
 * @Date: 2020/2/25 下午3:38
 */
@Configuration
public class DynamicConfigInit {

    //资源信息元数据:PropertySource包含name和泛型，一份资源信息存在唯一的name以及对应泛型数据
    public static final String DYNAMIC_CONFIG_NAME = "dynamic_config";

    @Autowired
    AbstractEnvironment environment;

    //k8s configmap 配置文件保存路径
    @Value("${configmap.path}")
    private String configmapPath;

    private DynamicLoadPropertySource propertySource;


    @PostConstruct
    public void init() {
        propertySource = new DynamicLoadPropertySource(DYNAMIC_CONFIG_NAME,configmapPath+"/app.properties");
        environment.getPropertySources().addFirst(propertySource);
    }

    public DynamicLoadPropertySource getPropertySource() {
        return propertySource;
    }

}