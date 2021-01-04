package cn.tongdun.kunpeng.api.application.platformindex;

import cn.tongdun.gaea.paas.api.GaeaApi;
import cn.tongdun.kunpeng.api.application.platformindex.impl.GaeaPaasServiceImpl;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * gaea-paas指标平台对接dubbo引用服务条件暴露
 * @author jie
 * @date 2020/12/16
 */
@Configuration
@ConditionalOnClass(name = {"cn.tongdun.gaea.paas.api.GaeaApi"})
public class GaeaPaasIndicatrixServiceConfiguration {

    @Value("${gaea.paas.dubbo.version}")
    private String gaeaPaasVersion;
    @Value("${gaea.paas.dubbo.timeout}")
    private Integer gaeaPaasTimeout;

    @Autowired
    private ApplicationConfig applicationConfig;
    @Autowired
    private ConsumerConfig consumerConfig;
    @Autowired
    private RegistryConfig registryConfig;


    @ConditionalOnClass(name = "cn.tongdun.gaea.paas.api.GaeaApi")
    @ConditionalOnProperty(name = {"gaea.paas.dubbo.version","gaea.paas.dubbo.timeout"})
    @Bean
    public GaeaApi gaeaApi(){
        ReferenceConfig<GaeaApi> newReferenceConfig = new ReferenceConfig<>();
        newReferenceConfig.setApplication(applicationConfig);
        newReferenceConfig.setConsumer(consumerConfig);
        newReferenceConfig.setRegistry(registryConfig);
        newReferenceConfig.setInterface(GaeaApi.class);
        newReferenceConfig.setVersion(gaeaPaasVersion);
        newReferenceConfig.setTimeout(gaeaPaasTimeout);
        newReferenceConfig.setConnections(2);
        newReferenceConfig.setGeneric(false);
        newReferenceConfig.setRetries(0);

        Map<String, String> parameters = Maps.newHashMapWithExpectedSize(5);
        parameters.put("threadname", "Dubbo-gaea-paas-");
        parameters.put("threadpool", "cached");
        parameters.put("corethreads", "2");
        parameters.put("threads", "10");
        parameters.put("alive", "600000");
        newReferenceConfig.setParameters(parameters);

        return newReferenceConfig.get();
    }

    @Bean
    @ConditionalOnBean(name = "gaeaApi",value = GaeaApi.class)
    public KpIndicatrixService gaeaPaasService(GaeaApi gaeaApi){
        GaeaPaasServiceImpl paasGaeaService = new GaeaPaasServiceImpl(gaeaApi);
        return paasGaeaService;
    }
}
