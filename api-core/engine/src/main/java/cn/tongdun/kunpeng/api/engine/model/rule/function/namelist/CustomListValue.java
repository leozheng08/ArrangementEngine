package cn.tongdun.kunpeng.api.engine.model.rule.function.namelist;

import cn.tongdun.kunpeng.api.engine.model.StatusEntity;

/**
 * Created by lvyadong on 2020/01/17.
 */
public class CustomListValue extends StatusEntity{

    private String customListUuid;
    private String value;
    private long expireTime;

    public CustomListValue() {

    }
    public CustomListValue(String value, long expireTime) {
        this.value = value;
        this.expireTime = expireTime;
    }

    public CustomListValue(String customListUuid,String value) {
        this.customListUuid = customListUuid;
        this.value = value;
    }

    public CustomListValue(String customListUuid,String value, long expireTime) {
        this.customListUuid = customListUuid;
        this.value = value;
        this.expireTime = expireTime;
    }

    public String getCustomListUuid() {
        return customListUuid;
    }

    public void setCustomListUuid(String customListUuid) {
        this.customListUuid = customListUuid;
    }

    public String getValue() {
        return value;
    }

    public long getExpireTime() {
        return expireTime;
    }
}
