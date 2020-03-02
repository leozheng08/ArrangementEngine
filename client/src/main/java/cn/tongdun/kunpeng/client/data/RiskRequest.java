package cn.tongdun.kunpeng.client.data;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ValueFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author: liang.chen
 * @Date: 2020/2/28 下午5:43
 */
public class RiskRequest implements Serializable {

    private static final Field[] fields = RiskRequest.class.getDeclaredFields();
    private static final Set<String> fieldNames = new HashSet<>(fields.length);
    static {
        Set<String> includeTypes = new HashSet<>();
        includeTypes.add("int");
        includeTypes.add("integer");
        includeTypes.add("string");
        includeTypes.add("double");
        includeTypes.add("long");
        includeTypes.add("float");
        includeTypes.add("boolean");
        for (Field field : fields) {
            String simTypeName = field.getType().getSimpleName();
            if (includeTypes.contains(simTypeName.toLowerCase())) {
                fieldNames.add(field.getName());
            }
        }
    }

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
    private String policyVersion;


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
    private String tokenId;


    /**
     * 手机的设备指纹
     */
    private String blackBox;

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
    private String simulationPartner;

    /**
     * 仿真的appName
     */
    private String simulationApp;

    /**
     * 仿真的seqId
     */
    private String simulationSeqId;
    private String simulationUuid;
    private String tdSampleDataId;

    /**
     * 按字段管理中定义的field_code传的字段值
     */
    private Map<String, Object> fieldValues = new HashMap<>();

    /**
     * 扩展属性
     */
    private Map<String, Object> extAttrs = new HashMap<>();



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

    public String getSimulationPartner() {
        return simulationPartner;
    }

    public void setSimulationPartner(String simulationPartner) {
        this.simulationPartner = simulationPartner;
    }

    public String getSimulationApp() {
        return simulationApp;
    }

    public void setSimulationApp(String simulationApp) {
        this.simulationApp = simulationApp;
    }

    public String getSimulationSeqId() {
        return simulationSeqId;
    }

    public void setSimulationSeqId(String simulationSeqId) {
        this.simulationSeqId = simulationSeqId;
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


    public Map<String, Object> getExtAttrs() {
        return extAttrs;
    }

    public void setExtAttrs(Map<String, Object> extAttrs) {
        this.extAttrs = extAttrs;
    }

    public void addExtAttrs(String attrName,Object value){
        this.extAttrs.put(attrName,value);
    }

    private String upperCaseFirstChar(String x) {
        if (x == null) {return null;}
        if (x.isEmpty()) {return "";}
        String firstChar = x.substring(0, 1);
        String exceptFirst = x.substring(1);
        return firstChar.toUpperCase() + exceptFirst;
    }

    public Object get(String key) {
        if (key == null) {
            return null;
        }

        if ("null".equals(key)) {
            return "null";
        }

        if (fieldNames.contains(key)) {
            String methodName = "get" + upperCaseFirstChar(key);
            try {
                Method method = this.getClass().getMethod(methodName);
                Object value = method.invoke(this);
                if (value != null) {
                    return value;
                }
            } catch (Exception e) {
                // ignore
            }
        }

        Object fieldValue = fieldValues.get(key);
        if (fieldValue != null) {
            return fieldValue;
        }

        Object extAttr = extAttrs.get(key);
        if (extAttr != null) {
            return extAttr;
        }
        return null;
    }

    public String getSimulationUuid() {
        return simulationUuid;
    }

    public void setSimulationUuid(String simulationUuid) {
        this.simulationUuid = simulationUuid;
    }

    @Override
    public String toString(){
        ValueFilter valueFilter = new ValueFilter(){
            @Override
            public Object process(Object object, String name, Object value){
                if("secretKey".equals(name)){
                    if(value == null){
                        return null;
                    }
                    if (StringUtils.isBlank(value.toString()) || StringUtils.length(value.toString()) != 32) {
                        return secretKey;
                    }
                    return StringUtils.left(secretKey, 6).concat(
                            StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(secretKey, 6),
                                    StringUtils.length(secretKey), "*"), "******"));
                }
                return value;
            }
        };
        return JSONObject.toJSONString(this, new ValueFilter[]{valueFilter});
    }

}
