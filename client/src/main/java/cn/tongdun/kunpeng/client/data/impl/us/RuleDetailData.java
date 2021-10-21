package cn.tongdun.kunpeng.client.data.impl.us;

import java.io.Serializable;

/**
 * @author: yuanhang
 * @date: 2020-06-17 15:54
 **/
public class RuleDetailData  implements Serializable {
    private static final long serialVersionUID = -76322312312234L;

    private String description;

    private String data;

    private String type;

    private String systemField;

    private String parentUuid;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSystemField() {
        return systemField;
    }

    public void setSystemField(String systemField) {
        this.systemField = systemField;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }
}
