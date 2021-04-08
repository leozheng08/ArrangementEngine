package cn.tongdun.kunpeng.api.common.data;

/**
 * Created by coco on 17/9/5.
 */
public enum ReasonCode {

    // 1~99：权限认证错误
    AUTH_FAILED("001", "001"), //


    // 100~199：数据和参数校验错误
    REQ_DATA_TYPE_ERROR("100", "请求数据类型或格式不正确"), //
    PARAM_NULL_ERROR("101", "参数不能为空"), //
    PARAM_DATA_TYPE_ERROR("102", "参数类型不正确"), //
    PARAM_OVER_MAX_LEN("103", "参数超过最大长度"), //
    PARAM_FORMAT_ERROR("104", "参数格式不正确"), //
    QUERY_TIME_INTERVAL_INVALID("105", "参数范围不正确"), //
    PARAM_DATA_NOT_EXIST_ERROR("106", "枚举值不存在"), //
    PARAM_RECALL_SEQID_ILLEGAL("108", "重试验证失败,重试seqId不合法"),
    PARAM_RECALL_HAVE_SUCCESS("109", "已重试成功,无法再次重试"),
    RECALL_TIME_TOO_LONG("110", "重试时间太久远,超过3个月"),


    LOAN_APPLY_ID_BLANK_ERROR("130", "申请编号不存在"),
    LOAN_APPLY_ID_PARTNER_ERROR("131", "申请编号非合作方所有"),
    LOAN_APPLY_ID_ID_NUMBER_ERROR("132", "申请编号与身份证不符"),
    LOAN_APPLY_ID_NOT_EXIST_ERROR("133", "申请编号过期"),


    SUCCESS("200", "处理成功"),

    // 300～399 客户账单错误
    NOT_BUY_SERVICE("301", "没有购买服务"), //
    NOT_ALLOWED("302", "已被禁用"), //
    FLOW_POOR("303", "流量不足"), //
    OUT_OF_SERVICE_DATE("304", "服务时间过期"), //
    NOT_ACCEPTABLE("305", "日调用次数已达上限"), //

    // 400~499：策略执行错误
    POLICY_NOT_EXIST("404", "没有对应的策略配置"), //
    POLICY_EXECUTE_TIMEOUT("405", "405"), //
    RECALL_DATA_EXIST("470", "重试查询不到数据"),

    // 500~599:服务器内部错误
    INTERNAL_ERROR("500", "服务器内部异常"),
    ENGINE_EXECUTE_ERROR("505", "505"),
    ENGINE_EXECUTE_TIMEOUT("506", "决策引擎运行异常"),
    DATA_NOT_READY("507", "部分数据未准备好"),
    SERVICE_FLOW_ERROR("508", "子服务流量不足"),
    ENCRYPTION_FIELD_NOT_READY("509","字段获取失败，部分数据获取不全"),
    RATE_LIMITING("600", "限流"),
    // 600~:其它
    NO_RESULT("666", "666"),
    CALCULATE_ERROR("701", "数据查询失败"),


    PARAM_NECESSARY_FIELD_NULL("10101", "缺少必传字段"),
    PARAM_NECESSARY_FIELD_TYPE_ERROR("10102", "必传字段格式校验失败"),
    PARAM_NECESSARY_FIELD_ATTRIBUTE_ERROR("10103", "必传字段属性读取失败"),
    PARAM_NECESSARY_FIELD_TIMEOUT("10104", "必传字段获取超时"),
    PARAM_NECESSARY_FIELD_ERROR("10105", "必传字段获取失败"),



    NO_PASS_DATA("20001", "客户没有传规则所需的参数"),
    NO_INDICATRIX("20002", "没有指标数据"),
    NO_NAME_LIST("20003", "没有名单数据"),
    NO_LINK_ASSOCIATION("20004", "未关联到风险群体"),
    NO_MODEL("20005", "没有模型数据"),
    NO_FP("20006", "没有设备指纹数据"),
    NO_KUNTA("20007", "没有三方数据"),
    NO_WATSON("20008", "没有地址服务数据"),
    NO_SCORE_CARD("20009", "没有智信分"),
    NO_GEOIP("20010", "没有GEOIP信息"),

    // 400~499：策略执行错误
    POLICY_NOT_EXIST_SUB("40401", "没有对应的策略配置"),
    POLICY_DELETED("40405", "策略已删除"),
    POLICY_CLOSED("40406", "策略已关闭"),
    SUB_POLICY_NOT_EXIST("40404","对应的策略下没有子策略"),
    RULE_NOT_EXIST("40402", "对应的策略下没有规则"),
    POLICY_LOAD_ERROR("40403", "策略加载有误"),
    SUB_POLICY_LOAD_ERROR("40407", "子策略加载有误"),
    RULE_LOAD_ERROR("40408", "规则加载有误"),


    //此状态码表示：部分数据获取失败的，仍旧返回调用成功，同时返回部分数据获取失败的状态码，不计费、记录事件；客户可以重试

    INDICATRIX_QUERY_ERROR("50701", "指标查询出错"),
    INDICATRIX_QUERY_TIMEOUT("50702", "指标查询超时"),
    NAME_LIST_QUERY_ERROR("50703", "名单库查询出错"),
    NAME_LIST_QUERY_TIMEOUT("50704", "名单库查询超时"),
    // Loan和Lending事件，velocity数据还是在cassandra中存储、查询。查询超时报50705
    CASSANDRA_QUERY_TIMEOUT("50705", "信贷事件查询数据超时"),
    THIRD_SERVICE_CALL_ERROR("50706", "三方调用出错"),
    THIRD_SERVICE_CALL_TIMEOUT("50707", "三方调用超时"),
    LINK_ASSOCIATION_QUERY_ERROR("50708", "复杂网络风险群体关联失败"),
    LINK_ASSOCIATION_QUERY_TIMEOUT("50709", "复杂网络风险群体关联超时"),
    MODEL_RUN_ERROR("50710", "模型执行失败"),
    MODEL_RUN_TIMEOUT("50711", "模型执行超时"),
    FP_QUERY_ERROR("50712", "设备指纹查询失败"),
    FP_QUERY_TIMEOUT("50713", "设备指纹查询超时"),
    NLAS_CALL_TIMEOUT("50714", "自然语言分析服务调用超时"),
    PORN_IDENTIFY_CALL_TIMEOUT("50715", "图像鉴别服务调用超时"),
    IP_REPUTATION_CALL_TIMEOUT("50716", "IP画像调用超时"),
    ADDRESS_SERVICE_CALL_ERROR("50717", "地址服务调用出错"),
    ADDRESS_SERVICE_CALL_TIMEOUT("50718", "地址服务调用超时"),
    GROOVY_EXECUTE_ERROR("50719", "动态脚本执行失败"),

    CREDIT_SCORE_SERVICE_CALL_TIMEOUT("50721", "智信分服务调用超时"),
    CREDIT_SCORE_SERVICE_CALL_ERROR("50722", "信用分内部调用失败"),
    MOBILE_SERVICE_CALL_TIMEOUT("50723", "手机画像调用超时"),
    CREDIT_LIST_DETAIL_SERVICE_CALL_TIMEOUT("50724", "信贷名单库详情服务调用超时"),

    HBASE_QUERY_TIMEOUT("50725", "Habse查询超时"),
    AEROSPIKE_QUERY_TIMEOUT("50726", "Aerospike查询超时"),
    // 非Loan和非Lending事件，velocity数据先查asp、查不到再查hbase。asp和hbase都查询超时，报50727
    VELOCITY_QUERY_TIMEOUT("50727", "非信贷事件查询数据超时"),

    BUDLE_SERVICE_CALL_ERROR("50728", "budle正则表达式调用出错"),
    BUDLE_SERVICE_CALL_TIMEOUT("50729", "budle正则表达式调用超时"),
    BUDLE_SERVICE_CALL_BREAKING("50730", "budle正则表达式调用熔断"),
    HBASE_QUERY_ERROR("50731", "hbase查询出错"),
    AEROSPIKE_QUERY_ERROR("50732", "Aerospike查询出错"),
    IP_REPUTATION_CALL_ERROR("50733", "IP画像调用出错"),
    NLAS_CALL_ERROR("50734", "自然语言分析服务调用出错"),
    MOBILE_SERVICE_CALL_ERROR("50735", "手机画像调用出错"),
    CREDIT_LIST_DETAIL_SERVICE_CALL_ERROR("50736", "信贷名单库详情服务调用出错"),
    INDICATRIX_QUERY_LIMITING("50737", "指标平台限流"),

    MAIL_PARAM_NOT_FOUND("50738", "用户未传递邮箱参数"),
    MAIL_MODEL_TIMEOUT_ERROR("50739", "邮件模型服务超时"),
    MAIL_MODEL_RANDOM_TIMEOUT_ERROR("50740", "邮件模型随机率服务超时"),
    MAIL_MODEL_REQUEST_FAILED("50741", "邮件模型服务请求5XX"),
    MAIL_MODEL_RANDOM_REQUEST_FAILED("50742", "邮件模型随机率服务请求5XX"),
    MAIL_MODEL_NOT_AVAILABLE_ERROR("50743", "邮件模型服务不可用"),
    MAIL_MODEL_RANDOM_NOT_AVAILABLE_ERROR("50744", "邮件模型随机率服务不可用"),
    MAIL_MODEL_OTHER_EXCEPTION("50745", "邮箱模型服务其他异常"),


    RULE_ENGINE_TIMEOUT("50601", "规则引擎运行超时"),
    RULE_ENGINE_ERROR("50602", "规则引擎运行异常"),
    FLOW_ENGINE_ERROR("50603", "决策流引擎运行异常"),

    GAEA_FLOW_ERROR("50801", "指标流量不足"),
    THIRD_FLOW_ERROR("50806", "三方流量不足"),
    CREDIT_SCORE_SERVICE_CALL_NOFLOW("50809", "智信分服务流量不足"),
    LINK_ASSOCIATION_FLOW_ERROR("50810", "复杂网络流量不足"),

    /**
     * 加密管理：针对非必传字段
     */
    ENCRYPTION_FIELD_ATTRIBUTE_ERROR("50901","字段属性读取失败，置空"),
    ENCRYPTION_FIELD_TYPE_ERROR("50902","字段格式校验失败，置空"),
    ENCRYPTION_FIELD_QUERY_ERROR("50903","字段获取失败，置空"),


    APPLICATION_RATE_LIMITING("60001", "forseti-api应用限流"),
    THIRD_SERVICE_FALLBACK("60002", "三方接口子服务降级"),

    CALCULATE_QUERY_ERROR("70101", "决策结果自定义数据查询失败"),
    NO_OUTPUT_FORMULA("70102", "决策结果自定义公式有误"),
    PART_CALCULATE_ERROR("70103", "部分计算失败"),

    USBIN_ERROR_OTHER("50746","查询cardbin数据异常"),
    USBIN_ERROR_TIMEOUT("50747","查询不到cardbin信息超时"),

    GEOIP_SERVICE_CALL_ERROR("50720", "geoip服务调用失败"),
    GEOIP_SERVICE_CALL_TIMEOUT("50760", "geoip服务调用查询超时"),
    GEOIP_ILLEGAL_ERROR("50761","查询geoip-us非法参数"),
    GEOIP_PARAM_ERROR("50762","查询geoip-us必要参数为空"),
    GEOIP_PERNISSION_ERROR("50763","查询geoip-us权限不足"),
    GEOIP_SERRVER_ERROR("50764","查询geoip-us内部服务错误"),
    ;

    private String code;
    private String description;

    ReasonCode(String code, String description) {
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
        if(code.equals(description)){
            return code;
        }

        return code + ":" + description;
    }
}
