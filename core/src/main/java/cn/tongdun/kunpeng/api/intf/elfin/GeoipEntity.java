package cn.tongdun.kunpeng.api.intf.elfin;

import java.io.Serializable;

/**
 * 项目: elfin
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2017/4/10 下午3:25
 * 描述:
 */
public class GeoipEntity implements Serializable {

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
    private float latitude;                            // 纬度, 精确到小数点后4位
    private float longitude;                           // 经度, 精确到小数点后4位
    private double exactLatitude;                    // 纬度, 精确到小数点后6位, 与IPIP保持一致
    private double exactLongitude;                   // 经度, 精确到小数点后6位, 与IPIP保持一致
    private String extra1 = "";
    private String extra2 = "";
    private String isp = "";                // 运营商
    private String ispId = "";
    private String type = "";
    private String desc = "";
    private String org;//组织
    private String timeZone;//时区
    private String utcTimeZone;//UTC时区
    private String postCode;//邮编
    private String countryCode;//国家区号
    private String countryLogogram;//国家简写

    public long getLip() {
        return lip;
    }

    public void setLip(long lip) {
        this.lip = lip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public double getExactLatitude() {
        return exactLatitude;
    }

    public void setExactLatitude(double exactLatitude) {
        this.exactLatitude = exactLatitude;
    }

    public double getExactLongitude() {
        return exactLongitude;
    }

    public void setExactLongitude(double exactLongitude) {
        this.exactLongitude = exactLongitude;
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

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getUtcTimeZone() {
        return utcTimeZone;
    }

    public void setUtcTimeZone(String utcTimeZone) {
        this.utcTimeZone = utcTimeZone;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryLogogram() {
        return countryLogogram;
    }

    public void setCountryLogogram(String countryLogogram) {
        this.countryLogogram = countryLogogram;
    }
}
