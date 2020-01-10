package cn.tongdun.kunpeng.api.intf.ip.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * ThirdRiskObjs
 *
 * @author pandy(潘清剑)
 * @date 16/7/29
 */
public class ThirdRiskObjs extends UpdateTime implements Serializable{
    private List<RiskHistoryObj> riskHistoryObjs;

    public List<RiskHistoryObj> getRiskHistoryObjs() {
        return riskHistoryObjs;
    }

    public void setRiskHistoryObjs(List<RiskHistoryObj> riskHistoryObjs) {
        this.riskHistoryObjs = riskHistoryObjs;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
