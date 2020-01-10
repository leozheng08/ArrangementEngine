package cn.tongdun.kunpeng.api.intf.mobile.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 项目: alliance
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2016/11/15 上午11:52
 * 描述:
 */
public class ActivityRegionObjs extends UpdateTime implements Serializable  {

    private List<ActivityRegionObj> activityRegionObjList;

    public List<ActivityRegionObj> getActivityRegionObjList() {
        return activityRegionObjList;
    }

    public void setActivityRegionObjList(List<ActivityRegionObj> activityRegionObjList) {
        this.activityRegionObjList = activityRegionObjList;
    }
}
