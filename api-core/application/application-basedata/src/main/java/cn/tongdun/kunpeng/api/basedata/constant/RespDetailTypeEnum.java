package cn.tongdun.kunpeng.api.basedata.constant;

/**
 * @Author: liuq
 * @Date: 2020/5/21 5:25 下午
 */
public enum RespDetailTypeEnum {
    /**
     * 设备指纹返回的详情信息
     */
    GEOIP("地理位置信息"),
    DEVICE("设备指纹信息"),
    DEVICE_ALL("全量Android数据"),
    ATTRIBUTION("身份证和手机号地理位置信息"),
    HIT_RULE_DETAIL("规则命中详情"),
    HIT_RULE_DETAIL_V3("规则命中详情V3"),
    CREDIT_SCORE("信用评分");
    private String display;

    private RespDetailTypeEnum(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
