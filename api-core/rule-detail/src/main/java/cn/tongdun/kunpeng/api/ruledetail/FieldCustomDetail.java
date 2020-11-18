package cn.tongdun.kunpeng.api.ruledetail;

import cn.fraudmetrix.module.tdrule.rule.FieldDetail;

/**
 * @author: yuanhang
 * @date: 2020-11-03 17:08
 **/
public class FieldCustomDetail extends FieldDetail {

    private String detailType = "field";

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
    }
}
