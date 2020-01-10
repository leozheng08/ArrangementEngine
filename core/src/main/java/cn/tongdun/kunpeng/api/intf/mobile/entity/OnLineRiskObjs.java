package cn.tongdun.kunpeng.api.intf.mobile.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 项目: alliance
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2016/11/7 上午11:42
 * 描述:
 */
public class OnLineRiskObjs extends UpdateTime implements Serializable {

    private List<OnLineRiskObj> onLineRiskObjList;

    public List<OnLineRiskObj> getOnLineRiskObjList() {
        return onLineRiskObjList;
    }

    public void setOnLineRiskObjList(List<OnLineRiskObj> onLineRiskObjList) {
        this.onLineRiskObjList = onLineRiskObjList;
    }
}
