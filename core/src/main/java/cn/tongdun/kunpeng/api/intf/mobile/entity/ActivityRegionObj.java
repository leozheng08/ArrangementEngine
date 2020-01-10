package cn.tongdun.kunpeng.api.intf.mobile.entity;

import java.io.Serializable;

/**
 * 项目: alliance
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2016/10/24 上午10:47
 * 描述: 坐标信息
 */
public class ActivityRegionObj implements Serializable {

    private String latitude;//纬度
    private String longitude;//经度
    private String province;//省
    private String city;//城市
    private String count;//这个点一共出现的次数

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
