package cn.tongdun.kunpeng.api.common.util;

import java.util.Date;

/**
 * @Author: liang.chen
 * @Date: 2020/4/28 下午2:38
 */
public class TimestampUtil {

    //是否精度到毫秒，当前数据库保存的时间为timestamp精度是到秒。如果要支持到毫秒则数据库的gmt_modify需要改为timestamp(3)
    private static final boolean millisecondAccuracy = false;

    public static int compare(long x, long y) {
        if(millisecondAccuracy){
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
        x = x/1000;
        y = y/1000;
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static int compare(Date x, long y) {
        return compare(x.getTime(),y);
    }

    public static int compare(Date x, Date y) {
        return compare(x.getTime(),y.getTime());
    }
}
