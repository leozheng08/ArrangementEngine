package cn.tongdun.kunpeng.api.intf.ip.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * 项目: horde
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2017/3/23 上午10:56
 * 描述: 风险标签
 */
public class RiskLabelObj implements Serializable {

    //标签
    private String label;
    //标签中文名
    private String labelName;
    //设计合作方数量
    private String partnerNum;
    //发生次数
    private String occurNum;
    //最近一次发现时间
    private String lastTime;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getPartnerNum() {
        return partnerNum;
    }

    public void setPartnerNum(String partnerNum) {
        this.partnerNum = partnerNum;
    }

    public String getOccurNum() {
        return occurNum;
    }

    public void setOccurNum(String occurNum) {
        this.occurNum = occurNum;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
