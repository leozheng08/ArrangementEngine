package cn.tongdun.kunpeng.api.intf.ip.entity;

import java.io.Serializable;

/**
 * 项目: horde
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 16/8/19 上午11:40
 * 描述:
 */
public class OnLineRiskObj implements Serializable{
    private String sequence_id;
    private String eventType;
    private String riskScore;
    private String eventOccurTime;

    public String getSequence_id() {
        return sequence_id;
    }

    public void setSequence_id(String sequence_id) {
        this.sequence_id = sequence_id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(String riskScore) {
        this.riskScore = riskScore;
    }

    public String getEventOccurTime() {
        return eventOccurTime;
    }

    public void setEventOccurTime(String eventOccurTime) {
        this.eventOccurTime = eventOccurTime;
    }
}
