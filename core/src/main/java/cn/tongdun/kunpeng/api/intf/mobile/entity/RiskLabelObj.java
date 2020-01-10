package cn.tongdun.kunpeng.api.intf.mobile.entity;

import java.io.Serializable;

/**
 * 项目: alliance
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2017/7/4 下午5:01
 * 描述:
 */
public class RiskLabelObj implements Serializable {

    private String label;
    private String occur_num;
    private String partner_num;
    private String last_time;
    private String delete;
    private String labelName;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOccur_num() {
        return occur_num;
    }

    public void setOccur_num(String occur_num) {
        this.occur_num = occur_num;
    }

    public String getPartner_num() {
        return partner_num;
    }

    public void setPartner_num(String partner_num) {
        this.partner_num = partner_num;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
