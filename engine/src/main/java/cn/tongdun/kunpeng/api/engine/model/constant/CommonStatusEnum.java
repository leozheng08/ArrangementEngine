package cn.tongdun.kunpeng.api.engine.model;

public enum CommonStatusEnum {

    CLOSE(0),
    OPEN(1);

    private int code;

    private CommonStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
