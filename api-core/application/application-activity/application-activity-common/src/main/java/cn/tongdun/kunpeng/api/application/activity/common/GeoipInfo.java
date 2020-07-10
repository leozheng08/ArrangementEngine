package cn.tongdun.kunpeng.api.application.activity.common;

import java.io.Serializable;

/**
 * @Author: changkai.yang
 * @Date: 2020/7/10 上午11:06
 */
public class GeoipInfo implements Serializable {
    private static final long serialVersionUID = -202078787878321L;

    /**
     * 国家名称
     */
    private String country;
    /**
     * 国家ID，ISO标准
     */
    private String countryId;
    /**
     * 省、直辖市、自治区
     */
    private String province;
    private String provinceId;
    /**
     * 城市
     */
    private String city;
    private String cityId;
    /**
     * 县、区
     */
    private String county;
    private String countyId;
    private String street;
    /**
     * 纬度
     */
    private float latitude;
    /**
     * 经度
     */
    private float longitude;

    private String isp = "";

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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }
}
