package cn.tongdun.kunpeng.api.consumer.util;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static void sleep(int millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
    }

    public static long timestamp() {
        Instant instant = Instant.now();
        return instant.toEpochMilli();
    }

    public static long getSeconds() {
        Instant instant = Instant.now();
        return instant.getEpochSecond();
    }

    /**
     * 当前时间增加入参时间后的值
     *
     * @param currDate
     * @param addTime
     * @param timeUnit
     * @return
     */
    public static Date addTime(Date currDate, Integer addTime, Integer timeUnit) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(currDate);
        ca.add(timeUnit, addTime);
        return ca.getTime();
    }

}
