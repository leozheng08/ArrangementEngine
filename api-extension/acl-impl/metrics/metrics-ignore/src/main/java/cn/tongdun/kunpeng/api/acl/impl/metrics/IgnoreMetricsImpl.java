package cn.tongdun.kunpeng.api.acl.impl.metrics;

import cn.tongdun.tdframework.core.metrics.IMetrics;
import cn.tongdun.tdframework.core.metrics.ITimeContext;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 监控统计的空实现
 *
 * @Author: liang.chen
 * @Date: 2020/3/27 下午5:24
 */
@Component
public class IgnoreMetricsImpl implements IMetrics {

    private final static ITimeContext IGNORE_TIME_CONTEXT = new ITimeContext() {
        @Override
        public long stop() {
            return 0;
        }
    };

    @Override
    public void counter(String counterName, String... tags) {
        //空操作
    }

    @Override
    public void counter(String counterName, String field, String... tags) {
        //空操作
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
        return IGNORE_TIME_CONTEXT;
    }

    @Override
    public void gaugeCollectionSize(String name, Collection collection) {
        //空操作
    }
}

