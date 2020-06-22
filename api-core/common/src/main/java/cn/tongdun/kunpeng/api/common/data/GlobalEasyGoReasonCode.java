package cn.tongdun.kunpeng.api.common.data;

/**
 * @author: yuanhang
 * @date: 2020-06-19 16:34
 **/
public enum GlobalEasyGoReasonCode {

    /**
     * 1~99：权限认证错误
     */
    AUTH_FAILED("001", "用户认证失败"),
    LICENSE_FAILED("002", "License验证失败"),
    /**
     * 100~199：数据和参数校验错误
      */
    REQ_DATA_TYPE_ERROR("100", "请求数据类型格式不正确"),
    PARAM_NULL_ERROR("101", "参数不能为空"),
    PARAM_DATA_TYPE_ERROR("102", "参数类型不正确"),
    PARAM_OVER_MAX_LEN("103", "参数超过最大长度"),
    PARAM_FORMAT_ERROR("104", "参数格式不正确"),
    QUERY_TIME_INTERVAL_INVALID("105", "查询时间间隔非法"),
    PARAM_DATA_NOT_EXIST_ERROR("106", "参数值不存在"),
    NAME_DATA_EXIST_ERROR("107", "名单已存在"),
    PARAM_NULL_IDENTIFY("108", "未传递事中标识"),
    PART_OF_DATA_IMPORT_ERROR("120", "部分数据导入失败"),
    ALL_OF_DATA_IMPORT_ERROR("121", "数据导入失败"),
    APP_NUM_ERROR("122","应用数超越此版本应有的版本数,请确认此版本的应用数"),
    // 300～399 客户账单错误
    NOT_BUY_SERVICE("301", "没有购买服务"),
    NOT_ALLOWED("302", "已被禁用"),
    FLOW_POOR("303", "流量不足"),
    OUT_OF_SERVICE_DATE("304", "服务时间过期"),

    // 400~499：策略执行错误
    POLICY_NOT_EXIST("404", "没有对应的策略配置"),
    POLICY_EXECUTE_TIMEOUT("405", "策略执行超时转成异步执行"),

    // 500~599:服务器内部错误
    INTERNAL_ERROR("500", "内部执行错误"),

    // 600~:其它
    NO_RESULT("600", "没有结果");

    private String code;
    private String description;

    GlobalEasyGoReasonCode(String code, String description) {
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
