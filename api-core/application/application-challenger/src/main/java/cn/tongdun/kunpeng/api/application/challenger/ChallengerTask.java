package cn.tongdun.kunpeng.api.application.challenger;

import cn.tongdun.kunpeng.client.data.RiskRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延时任务
 * @author hls
 * @version 1.0
 * @date 2022/8/9 7:11 下午
 */
@Data
public class ChallengerTask implements Delayed {
    //延迟时间
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private long time;

    /**
     * 任务的名称
     */
    private String name;

    /**
     * 当前的入参
     */
    private RiskRequest requestData;

    public ChallengerTask(String name, long time, TimeUnit unit, RiskRequest requestData) {
        this.name = name;
        this.time = System.currentTimeMillis() + (time > 0 ? unit.toMillis(time) : 0);
        this.requestData = requestData;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return time - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        ChallengerTask Order = (ChallengerTask) o;
        long diff = this.time - Order.time;
        if (diff <= 0) {
            return -1;
        } else {
            return 1;
        }
    }
}
