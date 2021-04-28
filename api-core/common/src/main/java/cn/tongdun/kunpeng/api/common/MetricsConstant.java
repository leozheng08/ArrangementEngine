package cn.tongdun.kunpeng.api.common;

/**
 * 打点监控公用配置
 * @author jie
 * @date 2021/1/14
 */
public class MetricsConstant {

    public static final String METRICS_TAG_PARTNER_KEY = "partner_code";
    public static final String METRICS_API_PARTNER_RT_KEY = "kunpeng.api.dubbo.partner.rt";

    /**
     * kunpeng 调用外部dubbo/http等接口打点标签
     */
    public static final String METRICS_TAG_API_QPS_KEY = "dubbo_qps";
    public static final String METRICS_API_QPS_KEY = "kunpeng.api.dubbo.qps";
    public static final String METRICS_API_RT_KEY = "kunpeng.api.dubbo.rt";
    public static final String METRICS_API_TIMEOUT_KEY = "kunpeng.api.dubbo.timeout";
    public static final String METRICS_API_CALL_ERROR_KEY = "kunpeng.api.dubbo.call_err";
    public static final String METRICS_API_BIZ_ERROR_KEY = "kunpeng.api.dubbo.biz_err";
    public static final String METRICS_API_OTHER_ERROR_KEY = "kunpeng.api.dubbo.other_err";
    public static final String METRICS_API_INTERNAL_ERROR_KEY = "kunpeng.api.dubbo.internal.error";
}
