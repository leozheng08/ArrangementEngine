package cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj;

/**
 * Created by coco on 17/10/13.
 */
public class ScriptDO  extends CommonDO {
    private String            uuid;
    private String            scriptName;
    private String            partnerCode;
    private String            appName;
    private String            eventType;
    private String            scriptCode;                             // 脚本代码
    private String            replicationField;                       // 赋值字段
    private String            description;
    private String            productCode;                              //业务产品标识
    private String            replicationFieldDisplayName;
    private String            eventTypeDisplayName;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getScriptCode() {
        return scriptCode;
    }

    public void setScriptCode(String scriptCode) {
        this.scriptCode = scriptCode;
    }

    public String getReplicationField() {
        return replicationField;
    }

    public void setReplicationField(String replicationField) {
        this.replicationField = replicationField;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getReplicationFieldDisplayName() {
        return replicationFieldDisplayName;
    }

    public void setReplicationFieldDisplayName(String replicationFieldDisplayName) {
        this.replicationFieldDisplayName = replicationFieldDisplayName;
    }

    public String getEventTypeDisplayName() {
        return eventTypeDisplayName;
    }

    public void setEventTypeDisplayName(String eventTypeDisplayName) {
        this.eventTypeDisplayName = eventTypeDisplayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScriptDO scriptDO = (ScriptDO) o;

        if (scriptName != null ? !scriptName.equals(scriptDO.scriptName) : scriptDO.scriptName != null) return false;
        if (partnerCode != null ? !partnerCode.equals(scriptDO.partnerCode) : scriptDO.partnerCode != null)
            return false;
        if (appName != null ? !appName.equals(scriptDO.appName) : scriptDO.appName != null) return false;
        if (eventType != null ? !eventType.equals(scriptDO.eventType) : scriptDO.eventType != null) return false;
        return productCode != null ? productCode.equals(scriptDO.productCode) : scriptDO.productCode == null;

    }

    @Override
    public int hashCode() {
        int result = scriptName != null ? scriptName.hashCode() : 0;
        result = 31 * result + (partnerCode != null ? partnerCode.hashCode() : 0);
        result = 31 * result + (appName != null ? appName.hashCode() : 0);
        result = 31 * result + (eventType != null ? eventType.hashCode() : 0);
        result = 31 * result + (productCode != null ? productCode.hashCode() : 0);
        return result;
    }
}
