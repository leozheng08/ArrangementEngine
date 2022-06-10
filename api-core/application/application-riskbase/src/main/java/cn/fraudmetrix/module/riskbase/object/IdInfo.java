package cn.fraudmetrix.module.riskbase.object;

import java.io.Serializable;
import java.util.Date;

public class IdInfo implements Serializable {

    private static final long serialVersionUID = 6136703218529933575L;

    private Date birthday;                               // 生日
    private Integer sex;                                    // 姓别：0女1男null未知
    private String province;                               // 省份
    private String city;                                   // 城市
    private String county;                                 // 区或县

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
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

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }
}
