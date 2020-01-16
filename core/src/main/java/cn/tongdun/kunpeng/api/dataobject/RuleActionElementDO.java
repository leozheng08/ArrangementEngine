package cn.tongdun.kunpeng.api.dataobject;


import java.sql.Timestamp;

/**
 * 规则操作
 */
public class RuleActionElementDO extends CommonDTO {

    private static final long serialVersionUID = 7768242359914265185L;

    private String fkRuleUuid;                   // rule 外键
    private String actions = "";                 // 操作
    private String createdBy = "";               // 创建者
    private String updatedBy = "";               // 修改者
    private String description = "";             // 描述


    public String getFkRuleUuid() {
        return fkRuleUuid;
    }

    public void setFkRuleUuid(String fkRuleUuid) {
        this.fkRuleUuid = fkRuleUuid;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }


    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "id: " + this.id + "\nuuid: " + this.uuid + "\nactions:" + this.actions + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RuleActionElementDO)) {
            return false;
        }

        RuleActionElementDO re = (RuleActionElementDO) obj;
        return this.uuid.equals(re.getUuid());
    }

    @Override
    public int hashCode() {
        final int prime = 15;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

}
