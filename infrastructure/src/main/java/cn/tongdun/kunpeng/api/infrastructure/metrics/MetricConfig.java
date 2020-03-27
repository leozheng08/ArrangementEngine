package cn.tongdun.kunpeng.api.infrastructure.metrics;

import cn.fraudmetrics.metrics.meta.CommonTag;
import cn.fraudmetrics.metrics.meta.IpProvider;
import cn.fraudmetrix.metrics.prometheus.PrometheusTool;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfig {

    private static Logger log = LoggerFactory.getLogger(MetricConfig.class);

    @Bean
    public PrometheusTool initPrometheusTool(PrometheusMeterRegistry prometheusMeterRegistry,
                                             CollectorRegistry collectorRegistry) throws Exception {
        try {
            return new PrometheusTool(prometheusMeterRegistry, collectorRegistry);
        } catch (Exception e) {
            log.error("Exception", e);
        }
        return null;
    }

    /**
     * @return
     * @throws Exception
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() throws Exception {
        CommonTag commonTag = new CommonTag();
        commonTag.addTags(ipProvider().provide()); //add ip tag
        commonTag.addTag("APPNAME","kunpeng-api"); //必须有这个tag
        return registry -> registry.config().commonTags(commonTag.commonTagsArray());
    }

    /**
     * optional add common tag ip
     *
     * @return
     */
    public IpProvider ipProvider() {
        return new IpProvider();
    }


}
