package cn.tongdun.kunpeng.api.intf.mobile.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 项目: alliance
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2017/7/4 下午4:59
 * 描述: 风险标签集合
 */
public class RiskLabelObjs extends UpdateTime implements Serializable {

    List<RiskLabelObj> riskLabelObjList;

    public List<RiskLabelObj> getRiskLabelObjList() {
        return riskLabelObjList;
    }

    public void setRiskLabelObjList(List<RiskLabelObj> riskLabelObjList) {
        this.riskLabelObjList = riskLabelObjList;
    }
}
