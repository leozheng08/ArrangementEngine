package cn.tongdun.kunpeng.api.dataobject;

import java.sql.Timestamp;
import java.util.List;

/**
 * 规则条件
 * 
 * @author du 2014年2月15日 下午5:09:28
 */
public class RuleConditionElementDO extends CommonDTO {

    private static final long            serialVersionUID   = 7768242359914265183L;

    private String                       parentUuid         = "";                  // 父节点
    private String                       logicOperator;                            // 逻辑操作符
    private String                       property;                                 // 属性
    private String                       operator           = "";                  // 操作符
    private String                       value              = "";                  // 条件值
    private String                       type;                                     // 条件值类型
    private Integer                      orderBy            = 0;                   // 排序
    private String                       version            = "";                  // 版本
    private String                       createdBy          = "";                  // 创建者
    private String                       updatedBy          = "";                  // 修改者
    private String                       description        = "";                  // 描述
    private String                       fkRuleUuid;                               // rule 外键
    private String                       params             = "";                  // 参数
    private String                       propertyDataType   = "";                  // 左变量类型
    private String                       rightValueType;                           // 右变量类型，可以是input，也可以是context
    private List<RuleConditionElementDO> children;                                 // 孩子节点
    private String                       descripe;                                 // 模板描述
    private boolean                      isLastChild;                              // 是否是最后一条 来加＂）＂结尾
    private String                       iterateType        = "any";
    private String                       metricId;                                 // 对应指标标识
    private boolean                      propertyUseOriginValue;                   // 是否是使用离线指标原始值，针对左右变量类型是平台指标的情况
    private String                       rightValueDataType = "";                  // 右变量类型，可以是GAEA_INDICATRIX、INT、BOOLEAN等

    public boolean isLastChild() {
        return isLastChild;
    }

    public void setLastChild(boolean isLastChild) {
        this.isLastChild = isLastChild;
    }

    public String getDescripe() {
        return descripe;
    }

    public void setDescripe(String descripe) {
        this.descripe = descripe;
    }

    public String getRightValueType() {
        return rightValueType;
    }

    public void setRightValueType(String rightValueType) {
        this.rightValueType = rightValueType;
    }

    public String getPropertyDataType() {
        return propertyDataType;
    }

    public void setPropertyDataType(String propertyDataType) {
        this.propertyDataType = propertyDataType;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public List<RuleConditionElementDO> getChildren() {
        return children;
    }

    public void setChildren(List<RuleConditionElementDO> children) {
        this.children = children;
    }

    public String getFkRuleUuid() {
        return fkRuleUuid;
    }

    public void setFkRuleUuid(String fkRuleUuid) {
        this.fkRuleUuid = fkRuleUuid;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    public String getLogicOperator() {
        return logicOperator;
    }

    public void setLogicOperator(String logicOperator) {
        this.logicOperator = logicOperator;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public String getIterateType() {
        return iterateType;
    }

    public void setIterateType(String iterateType) {
        this.iterateType = iterateType;
    }

    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    public boolean isPropertyUseOriginValue() {
        return propertyUseOriginValue;
    }

    public void setPropertyUseOriginValue(boolean propertyUseOriginValue) {
        this.propertyUseOriginValue = propertyUseOriginValue;
    }

    public String getRightValueDataType() {
        return rightValueDataType;
    }

    public void setRightValueDataType(String rightValueDataType) {
        this.rightValueDataType = rightValueDataType;
    }

    @Override
    public String toString() {
        return "id: " + this.id + "\nuuid: " + this.uuid + "\nparentUUID:" + this.parentUuid + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RuleConditionElementDO)) {
            return false;
        }

        RuleConditionElementDO re = (RuleConditionElementDO) obj;
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
