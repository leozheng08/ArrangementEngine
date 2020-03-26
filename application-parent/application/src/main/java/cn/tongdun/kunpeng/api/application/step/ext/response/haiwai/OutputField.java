package cn.tongdun.kunpeng.api.application.step.ext.response.haiwai;

import cn.tongdun.kunpeng.client.data.IOutputField;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by qiangzi on 17/1/12.
 */
public class OutputField implements IOutputField{

    private static final long    serialVersionUID = -3320502789769293390L;

    @JsonProperty("field_name")
    private String fieldName;
    private Object value;
    private String type;
    private String desc;

    public OutputField() {
    }

    public OutputField(String fieldName, Object value, String type, String desc) {
        this.fieldName = fieldName;
        this.value = value;
        this.type = type;
        this.desc = desc;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OutputField that = (OutputField) o;

        return fieldName != null ? fieldName.equals(that.fieldName) : that.fieldName == null;

    }

    @Override
    public int hashCode() {
        return fieldName != null ? fieldName.hashCode() : 0;
    }
}
