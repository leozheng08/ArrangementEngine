package cn.tongdun.kunpeng.api.intf.elfin;

import java.io.Serializable;

/**
 * 项目: elfin
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2017/10/1 下午2:10
 * 描述:
 */
public class BankCardEntity implements Serializable {

    private String binCode;        //卡bin
    private String binCode2;       //同卡bin区分
    private String bank;        //发卡行名称
    private String headBank;    //发卡总行名称
    private String country;    //发卡国家
    private String province;    //发卡行所在省份
    private String city;        //发卡行所在城市

    public String getBinCode() {
        return binCode;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }

    public String getBinCode2() {
        return binCode2;
    }

    public void setBinCode2(String binCode2) {
        this.binCode2 = binCode2;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getHeadBank() {
        return headBank;
    }

    public void setHeadBank(String headBank) {
        this.headBank = headBank;
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
}
