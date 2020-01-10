package cn.tongdun.kunpeng.api.intf.ip.entity;

import java.io.Serializable;

/**
 * PassiveDnsPojo
 * 对象类
 *
 * @author pandy(潘清剑)
 * @date 16/6/15
 */
public class PassiveDnsObj implements Serializable {
    /**
     * DNS的值
     */
    private String value;
    /**
     * DNS类型
     */
    private String type;
    /**
     * 记录类型
     */
    private String recordType;
    /**
     * 最近更新时间(第三方)
     */
    private String last;
    /**
     * 首次更新时间(第三方)
     */
    private String first;
    /**
     * 来源
     */
    private String source;

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getRecordType() {
        return recordType;
    }

    public String getLast() {
        return last;
    }

    public String getFirst() {
        return first;
    }

}
