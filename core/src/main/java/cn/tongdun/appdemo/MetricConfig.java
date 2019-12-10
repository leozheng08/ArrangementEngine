package cn.tongdun.appdemo;

import cn.fraudmetrix.metrics.meta.ServerInfoProvider;
import cn.fraudmetrix.metrics.meta.TagProvider;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

@Configuration
public class MetricConfig {

    @Autowired
    private MeterRegistry registry;

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public TagProvider serverInfoProvider() {
        return new ServerInfoProvider();
    }

    @PostConstruct
    public void init() throws Exception {
        Map<String, String> tags = serverInfoProvider().provide();
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            registry.config().commonTags(entry.getKey(), entry.getValue());
        }
    }
}
