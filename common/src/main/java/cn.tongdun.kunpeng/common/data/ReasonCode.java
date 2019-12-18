package cn.tongdun.kunpeng.common.data;

/**
 * Created by coco on 17/9/5.
 */
public enum ReasonCode {

    //错误码参看ReasonCodeEnum类，以后统一写在这里
    PARAM_RECALL_SEQID_ILLEGAL("108","重试验证失败,重试seqId不合法"),
    PARAM_RECALL_HAVE_SUCCESS("109","已重试成功,无法再次重试"),
    RECALL_TIME_TOO_LONG("110","重试时间太久远,超过3个月"),
    SUCCESS("200","处理成功"),
    NO_PASS_DATA("20001","客户没有传规则所需的参数"),
    NO_INDICATRIX("20002","没有指标数据"),
    NO_NAME_LIST("20003","没有名单数据"),
    NO_LINK_ASSOCIATION("20004","未关联到风险群体"),
    NO_MODEL("20005","没有模型数据"),
    NO_FP("20006","没有设备指纹数据"),
    NO_KUNTA("20007","没有三方数据"),
    NO_WATSON("20008","没有地址服务数据"),

    // 400~499：策略执行错误
    POLICY_NOT_EXIST("404", "没有对应的策略配置"), //
    POLICY_NOT_EXIST_SUB("40401","没有对应的策略配置"),
    RULE_NOT_CONFIG("40402","对应的策略下没有规则"),
    POLICY_LOAD_ERROR("40403","策略加载有误"),
    RULE_NOT_FIND("40404","没有对应的规则配置"),
    SUB_POLICY_NOT_FIND("40405","没有对应的子规则配置"),


    RECALL_DATA_EXIST("470","重试查询不到数据"),

    RULE_ENGINE_ERROR("50501", "规则引擎运行异常"),
    RULE_ENGINE_TIMEOUT("50601", "规则引擎运行超时"),

    //此状态码表示：部分数据获取失败的，仍旧返回调用成功，同时返回部分数据获取失败的状态码，不计费、记录事件；客户可以重试
    DATA_NOT_READY("507", "部分数据未准备好"),
    SERVICE_FLOW_ERROR("508", "子服务流量不足"),
    INDICATRIX_QUERY_ERROR("50701","指标查询出错"),
    INDICATRIX_QUERY_TIMEOUT("50702","指标查询超时"),
    NAME_LIST_QUERY_ERROR("50703","名单库查询出错"),
    NAME_LIST_QUERY_TIMEOUT("50704","名单库查询超时"),
    // Loan和Lending事件，velocity数据还是在cassandra中存储、查询。查询超时报50705
    CASSANDRA_QUERY_TIMEOUT("50705","信贷事件查询数据超时"),
    THIRD_SERVICE_CALL_ERROR("50706","三方调用出错"),
    THIRD_SERVICE_CALL_TIMEOUT("50707","三方调用超时"),
    LINK_ASSOCIATION_QUERY_ERROR("50708","复杂网络风险群体关联失败"),
    LINK_ASSOCIATION_QUERY_TIMEOUT("50709","复杂网络风险群体关联超时"),
    MODEL_RUN_ERROR("50710","模型执行失败"),
    MODEL_RUN_TIMEOUT("50711","模型执行超时"),
    FP_QUERY_ERROR("50712","设备指纹查询失败"),
    FP_QUERY_TIMEOUT("50713","设备指纹查询超时"),
    NLAS_CALL_TIMEOUT("50714","自然语言分析服务调用超时"),
    PORN_IDENTIFY_CALL_TIMEOUT("50715","图像鉴别服务调用超时"),
    IP_REPUTATION_CALL_TIMEOUT("50716","IP画像调用超时"),
    ADDRESS_SERVICE_CALL_ERROR("50717","地址服务调用出错"),
    ADDRESS_SERVICE_CALL_TIMEOUT("50718","地址服务调用超时"),
    GROOVY_EXECUTE_ERROR("50719","动态脚本执行失败"),
    GEOIP_SERVICE_CALL_ERROR("50720","GEOIP服务调用失败"),
    CREDIT_SCORE_SERVICE_CALL_TIMEOUT("50721","智信分服务调用超时"),
    CREDIT_SCORE_SERVICE_CALL_ERROR("50722","信用分内部调用失败"),
    MOBILE_SERVICE_CALL_TIMEOUT("50723","手机画像调用超时"),
    CREDIT_LIST_DETAIL_SERVICE_CALL_TIMEOUT("50724","信贷名单库详情服务调用超时"),

    HBASE_QUERY_TIMEOUT("50725","Habse查询超时"),
    AEROSPIKE_QUERY_TIMEOUT("50726","Aerospike查询超时"),
    // 非Loan和非Lending事件，velocity数据先查asp、查不到再查hbase。asp和hbase都查询超时，报50727
    VELOCITY_QUERY_TIMEOUT("50727","非信贷事件查询数据超时"),

    BUDLE_SERVICE_CALL_ERROR("50728","budle正则表达式调用出错"),
    BUDLE_SERVICE_CALL_TIMEOUT("50729","budle正则表达式调用超时"),
    BUDLE_SERVICE_CALL_BREAKING("50730","budle正则表达式调用熔断"),
    HBASE_QUERY_ERROR("50731","hbase查询出错"),
    AEROSPIKE_QUERY_ERROR("50732","Aerospike查询出错"),
    IP_REPUTATION_CALL_ERROR("50733","IP画像调用出错"),
    NLAS_CALL_ERROR("50734","自然语言分析服务调用出错"),
    MOBILE_SERVICE_CALL_ERROR("50735","手机画像调用出错"),
    CREDIT_LIST_DETAIL_SERVICE_CALL_ERROR("50736","信贷名单库详情服务调用出错"),
    INDICATRIX_QUERY_LIMITING("50737","指标平台限流"),

    RULE_RUN_EXCEPTION("50740","规则引擎执行异常"),


    GAEA_FLOW_ERROR("50801", "指标流量不足"),
    THIRD_FLOW_ERROR("50806", "三方流量不足"),
    CREDIT_SCORE_SERVICE_CALL_NOFLOW("50809","智信分服务流量不足"),
    LINK_ASSOCIATION_FLOW_ERROR("50810","复杂网络流量不足"),

    RATE_LIMITING("600", "限流"),
    APPLICATION_RATE_LIMITING("60001", "forseti-api应用限流"),
    THIRD_SERVICE_FALLBACK("60002", "三方接口子服务降级"),


    CALCULATE_ERROR("701", "数据查询失败"),
    CALCULATE_QUERY_ERROR("70101","决策结果自定义数据查询失败"),
    NO_OUTPUT_FORMULA("70102","决策结果自定义公式有误"),
    PART_CALCULATE_ERROR("70103","部分计算失败"),
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
        return code + ":" + description;
    }
}
