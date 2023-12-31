package cn.tongdun.kunpeng.api.engine.model.constant;

public enum CommonStatusEnum {

    CLOSE(0),
    OPEN(1),
    STOP(2);

    private int code;

    private CommonStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
