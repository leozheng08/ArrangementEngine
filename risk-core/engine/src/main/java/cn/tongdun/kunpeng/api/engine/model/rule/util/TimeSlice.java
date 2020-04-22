package cn.tongdun.kunpeng.api.engine.model.rule.util;

import java.io.Serializable;


public class TimeSlice implements Serializable {
    public static final String YEAR = "y";
    public static final String MONTH = "M";
    public static final String WEEK = "w";
    public static final String DAY = "d";
    public static final String HOUR = "h";
    public static final String MINUTE = "m";
    public static final String SECOND = "s";



    public static String getTimeSliceUnitDisplayName(String timeSliceUnit) {
        if (null == timeSliceUnit) {
            return null;
        }
        switch (timeSliceUnit) {
            case YEAR:
                return "年";
            case MONTH:
                return "月";
            case DAY:
                return "天";
            case HOUR:
                return "小时";
            case MINUTE:
                return "分钟";
            case SECOND:
                return "秒";
            default:
                return timeSliceUnit;
        }
    }

}
