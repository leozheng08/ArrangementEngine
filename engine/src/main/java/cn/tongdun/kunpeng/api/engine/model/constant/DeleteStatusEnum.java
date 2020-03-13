package cn.tongdun.kunpeng.api.engine.model.constant;

/**
 * @Author: liang.chen
 * @Date: 2020/3/13 下午3:31
 */
public enum DeleteStatusEnum {
    VALID(false),
    INVALID(true);

    private boolean code;

    private DeleteStatusEnum(boolean code) {
        this.code = code;
    }

    public boolean getCode() {
        return this.code;
    }
}
