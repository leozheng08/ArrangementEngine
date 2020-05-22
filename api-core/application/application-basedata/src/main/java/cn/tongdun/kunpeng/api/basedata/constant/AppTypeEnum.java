package cn.tongdun.kunpeng.api.basedata.constant;

/**
 * @Author: liuq
 * @Date: 2020/5/22 2:56 下午
 */
public enum AppTypeEnum {
    /**
     * 设备指纹端类型
     */
    web("网站"), ios("ios"), android("安卓");

    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private AppTypeEnum(String displayName){
        this.displayName = displayName;
    }
}
