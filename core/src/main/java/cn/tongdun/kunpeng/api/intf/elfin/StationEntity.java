package cn.tongdun.kunpeng.api.intf.elfin;

import java.io.Serializable;

/**
 * 项目: elfin
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2017/4/11 上午11:22
 * 描述:
 */
public class StationEntity implements Serializable {

    private long ipBegin;
    private long endBegin;
    private String country;
    private String province;
    private String city;
    private String org;
    private String isp;

    public long getIpBegin() {
        return ipBegin;
    }

    public void setIpBegin(long ipBegin) {
        this.ipBegin = ipBegin;
    }

    public long getEndBegin() {
        return endBegin;
    }

    public void setEndBegin(long endBegin) {
        this.endBegin = endBegin;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }
}
