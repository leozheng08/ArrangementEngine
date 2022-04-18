/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.fraudmetrix.module.riskbase.object;

import cn.fraudmetrix.module.riskbase.common.CommonDO;

import java.util.Map;

/**
 * 手机号标识段，前7位
 *
 * @author zhaohuabin 2014年6月18日 下午4:57:18
 * TODO 兼容过度阶段脚本，线上稳定后删除即可
 */
public class MobileInfoDO extends CommonDO {

    private static final long serialVersionUID = -6880357692217031977L;

    /**
     * 手段号段，前7位
     */
    private String phoneNumber;
    private String provinceCode;
    private String cityCode;
    private String operators;
    private String province;
    private String city;
    private Integer type;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getOperators() {
        return operators;
    }

    public void setOperators(String operators) {
        this.operators = operators;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void assignValue(Map<String, String> map) {
        for (String key : map.keySet()) {
            if (key.equals("phone_number")) {
                this.setPhoneNumber(map.get(key));
            } else if (key.equals("operators")) {
                this.setOperators(map.get(key));
            } else if (key.equals("province_code")) {
                this.setProvinceCode(map.get(key));
            } else if (key.equals("city_code")) {
                this.setCityCode(map.get(key));
            } else if (key.equals("type")) {
                this.setType(Integer.valueOf(map.get(key)));
            }
        }
    }
}
