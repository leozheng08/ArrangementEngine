package cn.tongdun.kunpeng.api.engine.metrics;

/**
 * @Author: liang.chen
 * @Date: 2020/3/27 下午5:49
 */
public interface IMetrics {

    /**
     * 计数
     * @param key
     * @param counterName
     * @param tags
     */
    void counter(String key,String counterName,String... tags);

    /**
     * 取平均值
     * @param key
     * @param counterName
     * @param num
     * @param tags
     */
    void summary(String key,String counterName,long num,String... tags);


    /**
     * 统计采样时间内的平均rt，单位为毫秒。
     * @param key
     * @param timerName
     * @param tags
     * @return
     */
    ITimeContext timer(String key,String timerName,String... tags);

    /**
     * 可以认为是Histogram和Timer的结合体，对rt进行Histogram统计，并会计算最近1分钟的tps，5分钟的tps，15分钟的tps，单位为纳秒。
     * @param key
     * @param timerName
     * @param tags
     * @return
     */
    ITimeContext metricTimer(String key,String timerName,String... tags);
}
