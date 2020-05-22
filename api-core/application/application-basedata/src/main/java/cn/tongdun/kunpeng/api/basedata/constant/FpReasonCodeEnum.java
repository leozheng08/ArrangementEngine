package cn.tongdun.kunpeng.api.basedata.constant;

/**
 * @Author: liuq
 * @Date: 2020/5/21 1:57 下午
 */
public enum FpReasonCodeEnum {
    /**
     * fp返回的错误常量
     */
    INNER_ERROR("500","内部错误"),
    DEVICE_RAW_ERROR("400","参数device_raw解析失败"),
    BLACK_BOX_ERROR("412","参数black_box解析失败"),
    INVOKE_TIMEOUT_ERROR("408","内部调用超时"),
    CONNECT_ERROR("504", "内部连接超时"),
    NO_FP_PARAM_ERROR("411","没有传递设备指纹参数"),
    NO_RESULT_ERROR("404","查无结果"),
    FMAGENT_INSTANCE_ERROR("413", "SDK没有初始化"),
    FAIL_ERROR_ENV("080", "传错环境"),
    ;

    private String code;
    private String description;

    private FpReasonCodeEnum(String code, String description){
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return code + ":" + description;
    }
}
