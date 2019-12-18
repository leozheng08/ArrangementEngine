package cn.tongdun.kunpeng.common.data;

import java.io.Serializable;

/**
 * Created by qiangzi on 17/1/12.
 */
public class OutputField implements Serializable{

    private static final long    serialVersionUID = -3320502789769293390L;
    private String field_name;
    private Object value;
    private String type;
    private String desc;

    public OutputField() {
    }

    public OutputField(String field_name, Object value, String type, String desc) {
        this.field_name = field_name;
        this.value = value;
        this.type = type;
        this.desc = desc;
    }

    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OutputField that = (OutputField) o;

        return field_name != null ? field_name.equals(that.field_name) : that.field_name == null;

    }

    @Override
    public int hashCode() {
        return field_name != null ? field_name.hashCode() : 0;
    }
}
