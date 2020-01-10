package cn.tongdun.kunpeng.api.intf.mobile.entity;

import java.io.Serializable;

/**
 * 项目: alliance
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2016/11/7 上午11:25
 * 描述:
 */
public class DeviceObj implements Serializable{
    //设备ID
    private String deviceId;
    //设备类型
    private String deviceType;
    //平台
    private String platform;
    //厂商
    private String factory;
    //型号
    private String type;
    //系统版本
    private String OSVersion;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOSVersion() {
        return OSVersion;
    }

    public void setOSVersion(String OSVersion) {
        this.OSVersion = OSVersion;
    }
}
