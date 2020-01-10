package cn.tongdun.kunpeng.api.intf.elfin;

import java.io.Serializable;
import java.util.Date;

/**
 * 项目: elfin
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2017/4/11 下午4:53
 * 描述: 手机归属地
 */
public class PhoneAttrEntity implements Serializable {

    private String phonePrefix;//手机号前缀7位

    private String province;    //省份

    private String city;        //城市

    private String isp;         //运营商

    private String type;        //手机卡类型

    private String typeName;    //手机卡类型中文名

    private Date updateTime;  //更新时间

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getPhonePrefix() {
        return phonePrefix;
    }

    public void setPhonePrefix(String phonePrefix) {
        this.phonePrefix = phonePrefix;
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

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
