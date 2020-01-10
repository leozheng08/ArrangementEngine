package cn.tongdun.kunpeng.api.intf.ip.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 项目: horde
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 16/8/19 上午11:35
 * 描述:
 */
public class OnLineRiskObjs extends UpdateTime implements Serializable {

    private List<OnLineRiskObj> onLineRiskObjs;

    public List<OnLineRiskObj> getOnLineRiskObjs() {
        return onLineRiskObjs;
    }

    public void setOnLineRiskObjs(List<OnLineRiskObj> onLineRiskObjs) {
        this.onLineRiskObjs = onLineRiskObjs;
    }
}
