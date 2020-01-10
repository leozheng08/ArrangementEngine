package cn.tongdun.kunpeng.api.intf.mobile.entity;

import java.io.Serializable;

/**
 * 项目: alliance
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2016/10/24 上午9:54
 * 描述:手机信息:包含手机画像的一些信息
 */
public class MobilPhoneInfosObj extends UpdateTime implements Serializable {

    private long phoneNO;
    private String phoneMD5;
    private String cacheTime = MobilPhoneRuleObj.timeTrans(System.currentTimeMillis());
//    2、虚假号码、通信小号识别,1天
    private BadAttributeObj badAttributeObj;

    //活跃区域,1天
    private ActivityRegionObjs activityRegionObjs;

    //常用设备,1天
    private UsuallyDevicesObj usuallyDevicesObj;

    //线上风险事件,1天
    private OnLineRiskObjs onLineRiskObjs;

    public long getPhoneNO() {
        return phoneNO;
    }

    public void setPhoneNO(long phoneNO) {
        this.phoneNO = phoneNO;
    }

    public BadAttributeObj getBadAttributeObj() {
        return badAttributeObj;
    }

    public void setBadAttributeObj(BadAttributeObj badAttributeObj) {
        this.badAttributeObj = badAttributeObj;
    }

    public UsuallyDevicesObj getUsuallyDevicesObj() {
        return usuallyDevicesObj;
    }

    public void setUsuallyDevicesObj(UsuallyDevicesObj usuallyDevicesObj) {
        this.usuallyDevicesObj = usuallyDevicesObj;
    }

    public OnLineRiskObjs getOnLineRiskObjs() {
        return onLineRiskObjs;
    }

    public void setOnLineRiskObjs(OnLineRiskObjs onLineRiskObjs) {
        this.onLineRiskObjs = onLineRiskObjs;
    }

    public ActivityRegionObjs getActivityRegionObjs() {
        return activityRegionObjs;
    }

    public void setActivityRegionObjs(ActivityRegionObjs activityRegionObjs) {
        this.activityRegionObjs = activityRegionObjs;
    }

    public String getPhoneMD5() {
        return phoneMD5;
    }

    public void setPhoneMD5(String phoneMD5) {
        this.phoneMD5 = phoneMD5;
    }

    public String getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(String cacheTime) {
        this.cacheTime = cacheTime;
    }
}
