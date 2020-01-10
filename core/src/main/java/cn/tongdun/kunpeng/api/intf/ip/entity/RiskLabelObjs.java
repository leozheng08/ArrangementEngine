package cn.tongdun.kunpeng.api.intf.ip.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 项目: horde
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2017/3/23 上午10:57
 * 描述:风险标签列表
 */
public class RiskLabelObjs extends UpdateTime implements Serializable {

    private List<RiskLabelObj> riskLabelObjs;

    public List<RiskLabelObj> getRiskLabelObjs() {
        return riskLabelObjs;
    }

    public void setRiskLabelObjs(List<RiskLabelObj> riskLabelObjs) {
        this.riskLabelObjs = riskLabelObjs;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
