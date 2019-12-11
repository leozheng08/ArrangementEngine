package cn.tongdun.kunpeng.api.dataobj;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Rosy on 17/7/5.
 */
@Data
public class RuleFieldDO extends CommonDO implements Serializable, Comparable<RuleFieldDO>, Cloneable{
    private static final long serialVersionUID = 8963907847949013871L;

    /**
     * 0 表示系统字段，1表示扩展字段。
     */
    private Integer sign;
    private String  partnerCode;
    private String  appName;
    private String  appType;                  // 系统字段适用的应用类型
    private String  name;
    private String  displayName;
    private String  createdBy;
    private String  modifiedBy;
    private String  type;
    private Integer maxLength;
    private String  description;
    private Integer necessary;
    private String  eventType;                // 事件类型
    private String  signForRule;              // 标示是否在规则配置左变量中展示
    private boolean signForVelocity;          // 标示是否作为主维度过滤
    private String  eventTypeDisplayName = "";
    private String  typeDisplayName      = "";
    private String  eventId;
    private String  appDisplayName;
    private String  partnerDisplayName;
    private String  appTypeName;              // 系统字段适用的应用类型展示名
    private String  fieldValue;
    private String  classDefinition;          // 对象定义文本(存uuid引用)
    private Boolean isArray;                  // 是否为数组
    private Boolean rootObject;             //是否是根对象
    private String  masterField="";             //指标平台需要用到的主属性字段
    private String  uuid;
    private String  uniqueName;                // 属性名（指标平台2期新增）

    private String  operationIp;             // 登录ip，为了操作日志增加

    private String attribute;  //字段属性列，用于追加字段所属类别——身份证、手机号、邮箱……

    public RuleFieldDO(){
        super();
    }

    public RuleFieldDO(String name, String displayName){
        super();
        this.name = name;
        this.displayName = displayName;
    }

    public RuleFieldDO(String name, String displayName, String type) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
    }

    public RuleFieldDO(String name, String displayName, String type, Boolean isArray) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.isArray = isArray;
    }

    @Override
    public String toString() {
        return this.name + ":" + this.displayName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        RuleFieldDO other = (RuleFieldDO) obj;
        if (eventType == null) {
            if (other.eventType != null) return false;
        } else if (!eventType.equals(other.eventType)) return false;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        return true;
    }

    @Override
    public int compareTo(RuleFieldDO o) {
        return this.getDisplayName().compareTo(o.getDisplayName());
    }

    public boolean isSystemField() {
        return new Integer(0).equals(this.getSign());
    }

    public String getFieldType(){
        if(this.getSign() == null){
            return "NULL";
        }
        int sign = this.getSign();
        switch (sign){
            case 0:
                return "SYSTEM";
            case 1:
                return "EXTEND";
            case 2:
                return "SYSTEM_OBJECT";
            default:
                return "NULL";
        }
    }

    public boolean isCustomField() {
        return new Integer(1).equals(this.getSign());
    }

    @Override
    public Object clone(){
        RuleFieldDO ruleFieldDO = null;
        try{
            ruleFieldDO = (RuleFieldDO)super.clone();
        }catch(CloneNotSupportedException e){
        }
        return ruleFieldDO;
    }

    /**
     * 避免名字被认为是boolean属性的getter，is写作iz<br/>
     * 1 为必填，2为非必填，将此逻辑转化为布尔。
     *
     * @return
     */
    public boolean izNecessary() {
        return getNecessary() != null && getNecessary() == 1;
    }
}
