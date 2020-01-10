package cn.tongdun.kunpeng.api.intf.mobile.entity;

import java.util.Set;

/**
 * 项目: alliance
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2016/10/24 下午3:58
 * 描述: 坏属性
 */
public class BadAttributeObj extends UpdateTime{

    private Set<String> badSet;

    public Set<String> getBadSet() {
        return badSet;
    }

    public void setBadSet(Set<String> badSet) {
        this.badSet = badSet;
    }
}
