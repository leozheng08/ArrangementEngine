/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.api.common.data;

import java.io.Serializable;

/**
 * @author zxb 2014年3月5日 下午4:35:29
 */
public class GeoipEntity implements Serializable {

    private static final long serialVersionUID = 6208506765313811814L;
    private long lip = 0;                // ip的整数表示形式，a.b.c.d->a*2^24+b*2^16+c*2^8+d
    private String ip = "";
    private String country = "";               // 国家名称
    private String countryId = "";               // 国家ID，ISO标准
    private String province = "";               // 省、直辖市、自治区
    private String provinceId = "";
    private String city = "";               // 城市
    private String cityId = "";
    private String county = "";               // 县、区
    private String countyId = "";
    private String area = "";
    private String areaId = "";
    private String address = "";
    private float latitude;                            // 纬度
    private float longitude;                           // 经度
    private String extra1 = "";
    private String extra2 = "";
    private String isp = "";
    private String ispId = "";
    private String type = "";
    private String desc = "";

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getLip() {
        return lip;
    }

    public void setLip(long lip) {
        this.lip = lip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String region) {
        this.province = region;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String regionId) {
        this.provinceId = regionId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountyId() {
        return countyId;
    }

    public void setCountyId(String countyId) {
        this.countyId = countyId;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public String getIspId() {
        return ispId;
    }

    public void setIspId(String ispId) {
        this.ispId = ispId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getExtra1() {
        return extra1;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }

    public String getExtra2() {
        return extra2;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "IpInfoEntity [lip=" + lip + ", ip=" + ip //
                + ", country=" + country + ", countryId=" + countryId //
                + ", area=" + area + ", areaId=" + areaId //
                + ", province=" + province + ", provinceId=" + provinceId //
                + ", city=" + city + ", cityId=" + cityId //
                + ", county=" + county + ", countyId=" + countyId //
                + ", isp=" + isp + ", ispId=" + ispId //
                + ", address=" + address //
                + ", lat=" + latitude + ", lon=" + longitude //
                + "]";
    }
}
