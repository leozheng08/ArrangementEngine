package cn.tongdun.kunpeng.api.engine.fp;

public class Anomaly {
    private String code;
    private String desc;
    private String descEn;

    public Anomaly(String code, String desc, String descEn) {
        this.code = code;
        this.desc = desc;
        this.descEn = descEn;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getDescEn() {
        return this.descEn;
    }
}
