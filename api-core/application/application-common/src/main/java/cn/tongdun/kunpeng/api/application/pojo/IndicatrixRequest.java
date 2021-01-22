package cn.tongdun.kunpeng.api.application.pojo;

import java.util.List;
import java.util.Map;

/**
 * 指标平台 接口调用参数定义
 * @author jie
 * @date 2020/12/14
 */
public class IndicatrixRequest {

    /**
     * 业务ID，一般用seqId，做业务对称匹配用
     */
    private String bizId;
    /**
     * 业务标识 forseti or simulation
     */
    private String bizName;
    private String partnerCode;
    private String eventType;
    private String eventId;
    private String appName;
    private Long eventOccurTime;
    /**
     * 要查询的指标编号集合
     */
    private List<Long> indicatrixIds;
    /**
     * 指标含义代码集合
     */
    private List<String> meaningCodes;
    /**
     * 当前业务事件数据
     */
    private Map<String, Object> activity;
    /**
     * 是否需要详情，默认不返回
     */
    private boolean needDetail;
    /**
     * 测试标记
     */
    private boolean testFlag = false;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Long getEventOccurTime() {
        return eventOccurTime;
    }

    public void setEventOccurTime(Long eventOccurTime) {
        this.eventOccurTime = eventOccurTime;
    }

    public List<Long> getIndicatrixIds() {
        return indicatrixIds;
    }

    public void setIndicatrixIds(List<Long> indicatrixIds) {
        this.indicatrixIds = indicatrixIds;
    }

    public List<String> getMeaningCodes() {
        return meaningCodes;
    }

    public void setMeaningCodes(List<String> meaningCodes) {
        this.meaningCodes = meaningCodes;
    }

    public Map<String, Object> getActivity() {
        return activity;
    }

    public void setActivity(Map<String, Object> activity) {
        this.activity = activity;
    }

    public boolean isNeedDetail() {
        return needDetail;
    }

    public void setNeedDetail(boolean needDetail) {
        this.needDetail = needDetail;
    }

    public boolean isTestFlag() {
        return testFlag;
    }

    public void setTestFlag(boolean testFlag) {
        this.testFlag = testFlag;
    }
}
