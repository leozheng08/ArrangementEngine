package cn.tongdun.kunpeng.api.infrastructure.metrics;

import cn.fraudmetrix.metrics.*;
import cn.fraudmetrix.metrics.prometheus.PromCounter;
import cn.fraudmetrix.metrics.prometheus.PromDistributionSummary;
import cn.fraudmetrix.metrics.prometheus.PromTimer;
import cn.fraudmetrix.metrics.prometheus.PrometheusTool;
import cn.tongdun.kunpeng.api.engine.metrics.IMetrics;
import cn.tongdun.kunpeng.api.engine.metrics.ITimeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author: liang.chen
 * @Date: 2020/3/27 下午5:24
 */
@Component
public class MetricsImpl implements IMetrics{

    private static final Logger logger                   = LoggerFactory.getLogger(MetricsImpl.class);


    @Autowired
    private PrometheusTool prometheusTool;

    @Override
    public void counter(String key,String counterName,String... tags){
        try {
            PromCounter promCounter =prometheusTool.promeCounter(counterName,tags);
            promCounter.mark();
        }catch (Exception e){
            logger.error("counter error",e);
        }
    }


    @Override
    public void summary(String key,String counterName,long num,String... tags){
        try {
            PromDistributionSummary promDistributionSummary =prometheusTool.promeDistributionSummary(counterName,tags);
            promDistributionSummary.record(num);
        }catch (Exception e){
            logger.error("summary error",e);
        }
    }



    @Override
    public ITimeContext timer(String key, String timerName, String... tags){
        try {
            PromTimer promTimer = prometheusTool.promTimer(timerName, tags);

            return new ITimeContext() {
                long start = System.currentTimeMillis();

                @Override
                public long stop() {
                    long end = System.currentTimeMillis();
                    long time = end - start;
                    promTimer.record(time, TimeUnit.MILLISECONDS);
                    return time;
                }
            };
        }catch (Exception e){
            logger.error("summary error",e);
        }
        return new ITimeContext() {
            @Override
            public long stop() {
                return 0;
            }
        };
    }


    @Override
    public ITimeContext metricTimer(String key, String timerName, String... tags){
        try {
            PromTimer promTimer = prometheusTool.promHistogramTimer(timerName, tags);

            return new ITimeContext() {
                long start = System.currentTimeMillis();

                @Override
                public long stop() {
                    long end = System.currentTimeMillis();
                    long time = end - start;
                    promTimer.record(time, TimeUnit.MILLISECONDS);
                    return time;
                }
            };
        }catch (Exception e){
            logger.error("summary error",e);
        }
        return new ITimeContext() {
            @Override
            public long stop() {
                return 0;
            }
        };
    }
}

