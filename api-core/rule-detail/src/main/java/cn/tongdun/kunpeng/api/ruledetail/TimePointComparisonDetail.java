package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;

import java.util.Date;


/**
 * 时间点比较
 * @Author: liang.chen
 * @Date: 2020/2/5 下午8:05
 */
@Data
public class TimePointComparisonDetail extends ConditionDetail {

    /**
     * 时间点的值，根据时间片取得当前的周数，小时数，或分钟数等
     */
    private Double result;

    /**
     * 时间
     */
    private Date time;

    /**
     * 时间片，如周、小时、分钟
     */
    private String timeSlice;

    public TimePointComparisonDetail() {
        super("time_point_comparison");
    }
}
