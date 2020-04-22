package cn.tongdun.kunpeng.api.engine.model.rule.function;

import java.util.Collections;

public enum VelocityFuncType {

    IS_FIRST("首次出现"),

    /**
     * 求次数
     */
    COUNT("次数"),

    /**
     * 关联个数
     */
    SET("不重复集合"),

    /**
     * 求次数
     */
    COUNT_2("个数"),
    /**
     * 求和
     */
    SUM("总和"),

    /**
     * 求平均
     */
    AVERAGE("平均值"),

    /**
     * 标准偏差
     */
    STDEV("标准偏差"),

    /**
     * 样本方差
     */
    VARIANCE("样本方差"),

    MIN("最小值"),
    MAX("最大值"),
    MIDDLE("中位数"),
    /**
     * 标准偏差
     */
    STANDARDDEVIATION("标准偏差"),

    /**
     * 求平台个数
     */
    PLATFORM_SUM("平台总数"),

    /**
     * 中位数
     */
    MEDIAN("中位数"),

    NORMAL_ADDR_COUNT("从属性地址次数"),

    ADDR_COUNT("主属性地址次数"),

    ADDR_ADDR_COUNT("主从属性地址次数"),

    NORMAL_ADDR_SET("从属性地址集合"),

    ADDR_NORMAL_SET("主属性地址集合"),

    ADDR_ADDR_SET("主从属性地址集合"),

    DAY_COUNT("活跃天数"),

    TIME_FIRST("最早时间"),

    TIME_LAST("最近时间"),

    IP_DISTANCE_SUM("IP距离之和"),

    GPS_DISTANCE_SUM("GPS距离之和"),

    CON_COUNT("连续次数"),

    TIME_INTERVAL_AVG("时间间隔平均值"),

    TIME_INTERVAL_MAX("时间间隔最大值"),

    TIME_INTERVAL_MIN("时间间隔最小值"),

    TIME_INTERVAL_MEDIAN("时间间隔中位数"),

    TIME_INTERVAL_VARIANCE("时间间隔方差"),

    TIME_INTERVAL_STDEV("时间间隔标准差"),

    POWER("幂"),
    INDEX("指数"),
    NATURAL_INDEX("自然指数"),
    LOGARITHM("对数"),
    NATURAL_LOGARITHM("自然对数");


    private final String displayName;

    VelocityFuncType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Object defaultValue() {
        switch (this) {
            case COUNT:
            case ADDR_COUNT:
            case DAY_COUNT:
            case CON_COUNT:
                return 0;
            case NORMAL_ADDR_COUNT:
            case ADDR_ADDR_COUNT:
                return Collections.emptyMap();
            case SET:
            case NORMAL_ADDR_SET:
            case ADDR_NORMAL_SET:
            case ADDR_ADDR_SET:
                return Collections.emptyList();
            case TIME_FIRST:
            case TIME_LAST:
                return null;
            default:
                return Double.NaN;
        }
    }


    public static void main(String[] args) {

    }

}
