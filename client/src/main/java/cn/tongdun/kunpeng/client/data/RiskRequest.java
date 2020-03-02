package cn.tongdun.kunpeng.client.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: liang.chen
 * @Date: 2020/2/28 下午5:43
 */
public class RiskRequest implements Serializable {

    /**
     * 合作方编码，必传
     */
    private String partnerCode;

    /**
     * app的secretKey，必传
     */
    private String secretKey;

    /**
     * 事件id，必传
     */
    private String eventId;

    /**
     * 策略版本号，非必填，如果不传则根据合作方、应用、eventId取得默认的决策版本号来运行。
     */
    String policyVersion;


    /**
     * 服务类型。比如基础版avalon，信贷云creditcloud,默认服务类型是专业版professional
     */
    private String serviceType;

    /**
     * 交易流水
     */
    private String sequenceId;

    /**
     * 链路跟踪相关的requestId
     */
    private String requestId;

    /**
     * 事件发生时间，如果合作方有传，以合作方为准，如果没传则取seqId中的时间戳
     */
    private Date eventOccurTime;

    /**
     * 测试标记
     */
    private boolean testFlag = false;

    /**
     * 如果async=true时，规则引擎运行不设超时时间。
     */
    private transient boolean async = false;


    /**
     * web的设备指纹会话id
     */
    String tokenId;


    /**
     * 手机的设备指纹
     */
    String blackBox;

    /**
     * 返回详细类型，包含有：application_id,geoip,device,device_all,attribution,hit_rule_detail,hit_rule_detail_v3,credit_score。可以指定去掉不需要返回的内容：-hit_rules,-policy_set_name,-policy_set,-policy_name,-output_fields
     */
    private String respDetailType;

    /**
     * 507、508的重试调用
     */
    private boolean recall = false;

    /**
     * 507、508的重试调用对应的原seqId
     */
    private String recallSequenceId;

    /**
     *  仿真的合作方编码
     */
    private String simulatePartnerCode;

    /**
     * 仿真的appName
     */
    private String simulateAppName;

    /**
     * 仿真的seqId
     */
    private String simulateSequenceId;
    private String tdSampleDataId;


    /**
     * 按字段管理中定义的field_code传的字段值
     */
    private Map<String, Object> fieldValues = new ConcurrentHashMap<>();


    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getPolicyVersion() {
        return policyVersion;
    }

    public void setPolicyVersion(String policyVersion) {
        this.policyVersion = policyVersion;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Date getEventOccurTime() {
        return eventOccurTime;
    }

    public void setEventOccurTime(Date eventOccurTime) {
        this.eventOccurTime = eventOccurTime;
    }

    public boolean isTestFlag() {
        return testFlag;
    }

    public void setTestFlag(boolean testFlag) {
        this.testFlag = testFlag;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getBlackBox() {
        return blackBox;
    }

    public void setBlackBox(String blackBox) {
        this.blackBox = blackBox;
    }

    public String getRespDetailType() {
        return respDetailType;
    }

    public void setRespDetailType(String respDetailType) {
        this.respDetailType = respDetailType;
    }

    public boolean isRecall() {
        return recall;
    }

    public void setRecall(boolean recall) {
        this.recall = recall;
    }

    public String getRecallSequenceId() {
        return recallSequenceId;
    }

    public void setRecallSequenceId(String recallSequenceId) {
        this.recallSequenceId = recallSequenceId;
    }

    public String getSimulatePartnerCode() {
        return simulatePartnerCode;
    }

    public void setSimulatePartnerCode(String simulatePartnerCode) {
        this.simulatePartnerCode = simulatePartnerCode;
    }

    public String getSimulateAppName() {
        return simulateAppName;
    }

    public void setSimulateAppName(String simulateAppName) {
        this.simulateAppName = simulateAppName;
    }

    public String getSimulateSequenceId() {
        return simulateSequenceId;
    }

    public void setSimulateSequenceId(String simulateSequenceId) {
        this.simulateSequenceId = simulateSequenceId;
    }

    public String getTdSampleDataId() {
        return tdSampleDataId;
    }

    public void setTdSampleDataId(String tdSampleDataId) {
        this.tdSampleDataId = tdSampleDataId;
    }

    public Map<String, Object> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(Map<String, Object> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public void setFieldValue(String fieldCode, Object value){
        fieldValues.put(fieldCode,value);
    }
}
