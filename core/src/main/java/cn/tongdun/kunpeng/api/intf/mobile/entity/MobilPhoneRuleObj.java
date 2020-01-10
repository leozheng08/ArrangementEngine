package cn.tongdun.kunpeng.api.intf.mobile.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 项目: alliance
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2017/7/4 下午3:07
 * 描述: 手机画像线上规则对象
 */
public class MobilPhoneRuleObj extends UpdateTime{

    private long phoneNo;

    private String phoneMD5;

    //缓存的时间,跟随缓存设置ttl时设置
    private String cacheTime = timeTrans(System.currentTimeMillis());

    //手机归属地
    private BaseAttributeObj baseAttributeObj;

    //风险标签
    private RiskLabelObjs riskLabelObjs;

    public long getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(long phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPhoneMD5() {
        return phoneMD5;
    }

    public void setPhoneMD5(String phoneMD5) {
        this.phoneMD5 = phoneMD5;
    }

    public BaseAttributeObj getBaseAttributeObj() {
        return baseAttributeObj;
    }

    public void setBaseAttributeObj(BaseAttributeObj baseAttributeObj) {
        this.baseAttributeObj = baseAttributeObj;
    }

    public RiskLabelObjs getRiskLabelObjs() {
        return riskLabelObjs;
    }

    public void setRiskLabelObjs(RiskLabelObjs riskLabelObjs) {
        this.riskLabelObjs = riskLabelObjs;
    }

    public String getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(String cacheTime) {
        this.cacheTime = cacheTime;
    }

    public static String timeTrans(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
}
