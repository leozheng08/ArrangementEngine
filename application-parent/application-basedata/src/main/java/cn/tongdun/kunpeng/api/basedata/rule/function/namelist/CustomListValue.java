package cn.tongdun.kunpeng.api.basedata.rule.function.namelist;

/**
 * Created by lvyadong on 2020/01/17.
 */
public class CustomListValue {

    private String value;
    private long expireTime;

    public CustomListValue(String value, long expireTime) {
        this.value = value;
        this.expireTime = expireTime;
    }

    public String getValue() {
        return value;
    }

    public long getExpireTime() {
        return expireTime;
    }
}
