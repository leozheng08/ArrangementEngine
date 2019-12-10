package cn.tongdun.appdemo.biz;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetricsDemo {

    @Autowired
    private MeterRegistry registry;

    /**
     * 使用注解记录时间，并且开启P95和P999统计
     */
    @Timed(percentiles = {0.95, 0.999}, histogram = true)
    public void execute1() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {

        }
    }

    /**
     * gauge, 计量用于采样数值的大小，比如下面采集的活动线程数
     */
    public void execute2() {
        registry.gauge("my_thread_active_count", Thread.activeCount());
    }

    /**
     * 统计不同合作方的调用次数
     * @param partnerCode
     */
    public void execute3(String partnerCode) {
        registry.counter("execute3_counter", "partner_code", partnerCode).increment();
    }

    /**
     * 自定义timer
     * @param partnerCode
     */
    public void execute4(String partnerCode) {
        Timer.Sample sample = Timer.start(registry);

        // do stuff
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {

        }

        sample.stop(Timer.builder("execute4_timer").
                tags("partner_code", partnerCode).
                publishPercentileHistogram().
                publishPercentiles(0.95, 0.999).register(registry));
    }
}
