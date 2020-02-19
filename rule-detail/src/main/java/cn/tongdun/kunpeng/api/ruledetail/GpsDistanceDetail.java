package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;

/**
 * 位置字段的距离计算，即计算位置A到位置B的距离
 * @Author: liang.chen
 * @Date: 2020/2/6 下午9:53
 */
@Data
public class GpsDistanceDetail extends ConditionDetail {

    private String gpsA;
    private String gpsADisplayName;
    private String gpsB;
    private String gpsBDisplayName;
    private Double result;
    private String unit;

    public GpsDistanceDetail(){
        super("gps_distance");
    }


}
