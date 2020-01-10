package cn.tongdun.kunpeng.api.intf.mobile.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 项目: alliance
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2016/11/7 上午11:35
 * 描述:常用设备
 */
public class UsuallyDevicesObj extends UpdateTime implements Serializable {

    private List<DeviceObj> deviceObjList;

    public List<DeviceObj> getDeviceObjList() {
        return deviceObjList;
    }

    public void setDeviceObjList(List<DeviceObj> deviceObjList) {
        this.deviceObjList = deviceObjList;
    }
}
