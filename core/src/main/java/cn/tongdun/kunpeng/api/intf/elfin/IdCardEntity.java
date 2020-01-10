package cn.tongdun.kunpeng.api.intf.elfin;

import java.io.Serializable;

/**
 * 项目: elfin
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2017/10/1 下午1:30
 * 描述:
 */
public class IdCardEntity implements Serializable {
    private String idNumPrefix;     //身份证前缀6位
    private String province;    //省份
    private String city;        //城市
    private String county;      //区、县
    private IdCardEntity original; //原出生时候的身份证归属地信息

    public String getIdNumPrefix() {
        return idNumPrefix;
    }

    public void setIdNumPrefix(String idNumPrefix) {
        this.idNumPrefix = idNumPrefix;
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

    public IdCardEntity getOriginal() {
        return original;
    }

    public void setOriginal(IdCardEntity original) {
        this.original = original;
    }
}
