package cn.tongdun.kunpeng.api.acl.impl.metrics;

import cn.fraudmetrix.metrics.Measurement;
import cn.fraudmetrix.metrics.MeasurementKey;
import cn.fraudmetrix.metrics.MetricTimer;
import cn.fraudmetrix.metrics.Registry;
import cn.tongdun.tdframework.core.metrics.IMetrics;
import cn.tongdun.tdframework.core.metrics.ITimeContext;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 监控统计的influxdb实现
 *
 * @Author: liang.chen
 * @Date: 2020/3/27 下午5:24
 */
@Component
public class InfluxDbMetricsImpl implements IMetrics {

    @Autowired
    private Registry registry;

    private final static ITimeContext IGNORE_TIME_CONTEXT = () -> 0;

    @Override
    public void counter(String counterName, String... tags) {
        MeasurementKey.MeasurementKeyBuilder builder = new MeasurementKey.MeasurementKeyBuilder(counterName);

        if (ArrayUtils.isNotEmpty(tags)) {
            for (int i = 0; i < tags.length - 1; i += 2) {
                builder.addTag(tags[i], tags[i + 1]);
            }
        }

        Measurement measurement = registry.getMeasurement(builder.build());
        measurement.counter("count").mark(1L);
        String[] counterNames = counterName.split(":");
        if (counterNames.length != 1) {
            counter(measurement, counterNames[1]);
        }

    }


    /**
     * 主要用来统计subReasonCode
     */
    public void counter(Measurement measurement, String countName) {
        measurement.counter(countName).mark(1L);
    }


    @Override
    public void summary(String counterName, long num, String... tags) {
        //空操作
    }


    @Override
    public ITimeContext timer(String timerName, String... tags) {
        return IGNORE_TIME_CONTEXT;
    }


    @Override
    public ITimeContext metricTimer(String timerName, String... tags) {
        MeasurementKey.MeasurementKeyBuilder builder = new MeasurementKey.MeasurementKeyBuilder(timerName);

        if (ArrayUtils.isNotEmpty(tags)) {
            for (int i = 0; i < tags.length - 1; i += 2) {
                builder.addTag(tags[i], tags[i + 1]);
            }
        }

        Measurement measurement = registry.getMeasurement(builder.build());
        return new ITimeContext() {
            long start = System.currentTimeMillis();
            MetricTimer.Context context = measurement.metricTimer("rt");

            @Override
            public long stop() {
                long end = System.currentTimeMillis();
                long time = end - start;
                context.stop();
                return time;
            }
        };
    }

    @Override
    public void gaugeCollectionSize(String name, Collection collection) {
        //空操作
    }
}

