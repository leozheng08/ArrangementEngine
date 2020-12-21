package cn.tongdun.kunpeng.api.application.platformindex;

import cn.tongdun.gaea.api.client.IndicatrixApi;
import cn.tongdun.kunpeng.api.application.platformindex.impl.GaeaApiAbnormalServiceImpl;
import cn.tongdun.kunpeng.api.application.platformindex.impl.GaeaApiLatencyServiceImpl;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.MethodConfig;
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
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 指标平台对接dubbo引用服务条件暴露
 * @author jie
 * @date 2020/12/16
 */
@Configuration
@ConditionalOnClass(name = {"cn.tongdun.gaea.api.client.IndicatrixApi"})
public class GaeaApiIndicatrixServiceConfiguration {

    @Value("${gaea.api.client.dubbo.version}")
    private String gaeaApiClientVersion;
    @Value("${gaea.api.client.dubbo.timeout}")
    private Integer gaeaApiTimeout;
    @Value("${gaea.api.client.dubbo.retries}")
    private Integer gaeaApiRetries;

    @Value("${gaea.api.client.abnormal.dubbo.timeout}")
    private Integer gaeaApiAbnormalTimeout;
    @Value("${gaea.api.client.latency.dubbo.timeout}")
    private Integer gaeaApiLatencyTimeout;

    @Autowired
    private ApplicationConfig applicationConfig;
    @Autowired
    private ConsumerConfig consumerConfig;
    @Autowired
    private RegistryConfig registryConfig;

    @Autowired
    private Environment environment;

    @ConditionalOnClass(name = "cn.tongdun.gaea.api.client.IndicatrixApi")
    @ConditionalOnProperty(name = {"gaea.client.dubbo.version","gaea.client.dubbo.retries","gaea.client.credit.dubbo.timeout","gaea.client.antifraud.dubbo.timeout"})
    @Bean
    public IndicatrixApi indicatrixApi(){
        ReferenceConfig<IndicatrixApi> newReferenceConfig = new ReferenceConfig<>();
        newReferenceConfig.setApplication(applicationConfig);
        newReferenceConfig.setConsumer(consumerConfig);
        newReferenceConfig.setRegistry(registryConfig);
        newReferenceConfig.setInterface(IndicatrixApi.class);
        newReferenceConfig.setVersion(gaeaApiClientVersion);
        newReferenceConfig.setTimeout(gaeaApiTimeout);
        newReferenceConfig.setConnections(2);
        newReferenceConfig.setGeneric(false);
        newReferenceConfig.setRetries(gaeaApiRetries);

        Map<String, String> parameters = Maps.newHashMapWithExpectedSize(5);
        parameters.put("threadname", "Dubbo-gaea-api-");
        parameters.put("threadpool", "cached");
        parameters.put("corethreads", "2");
        parameters.put("threads", "10");
        parameters.put("alive", "600000");
        newReferenceConfig.setParameters(parameters);

        // 方法级配置
        List<MethodConfig> methods = new ArrayList<MethodConfig>();
        MethodConfig method = new MethodConfig();
        method.setName("calculateByIdForAbnormalSensitive");
        method.setTimeout(gaeaApiAbnormalTimeout);
        methods.add(method);

        MethodConfig latencyMethod = new MethodConfig();
        latencyMethod.setName("calculateByIdForLatencySensitive");
        latencyMethod.setTimeout(gaeaApiLatencyTimeout);
        methods.add(latencyMethod);

        return newReferenceConfig.get();
    }

    @Bean
    @ConditionalOnBean(name = "indicatrixApi")
    public KpIndicatrixService gaeaApiLatencyService(IndicatrixApi indicatrixApi){
        GaeaApiLatencyServiceImpl latencyGaeaService = new GaeaApiLatencyServiceImpl(indicatrixApi);
        return latencyGaeaService;
    }

    @Bean
    @ConditionalOnBean(name = "indicatrixApi")
    public KpIndicatrixService gaeaApiAbnormalService(IndicatrixApi indicatrixApi){
        GaeaApiAbnormalServiceImpl saasAbnormalGaeaService = new GaeaApiAbnormalServiceImpl(indicatrixApi);
        return saasAbnormalGaeaService;
    }
}
