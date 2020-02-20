package cn.tongdun.kunpeng.client.data;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 风险扫描返回结果
 * 
 * @author zxb 2014年3月12日 上午11:00:59
 */
@Data
public class RiskResponse extends ApiResponse implements Cloneable {

    private static final long         serialVersionUID    = 844958112006659504L;
    private Integer                   final_score;                                  // 风险分数
    private String                    final_decision;                               // 最终的风险决策结果
    private String                    policy_name;                                  // 策略名称
    private List<HitRule>             hit_rules;                                    // 命中规则列表
    private String                    seq_id;                                       // 请求序列号，每个请求进来都分配一个全局唯一的id
    private Integer                   spend_time;                                   // 花费的时间，单位ms
    private Map<String, String>       geoip_info;                                   // 地理位置信息
    private Map<String, Object>       device_info;                                  // 设备指纹信息
    private Map<String, String>       attribution;                                  // 归属地信息
    private Map<String, Object>       hitRuleDetail_info;                           // 规则命中详情
    private Map<String, Object>       hit_rule_detail_v3_info;                      // 规则命中详情V3
    private List<PolicyResponse>      policy_set;                                   // 策略集信息
    private String                    policy_set_name;                              // 策略集名称
    private String                    risk_type;                                    // 风险类型
    private String                    application_id;                               // 借款事件申请编号
    @JSONField(serialize = false, deserialize = false)
    private boolean                   isContainApplicationId;                       // 是否返回申请编号,用于在consumer统计有多少合作方返回了申请编号
    @JSONField(serialize = false, deserialize = false)
    private Boolean                   flowChargeSuccessed = false;                  // 是否成功调用流量
    @JSONField(serialize = false, deserialize = false)
    private Boolean                   emergencySwithcOn   = false;                  // 应急开关状态,默认为关闭
    @JSONField(serialize = false, deserialize = false)
    private DecisionType              decisionType        = DecisionType.POLICY_SET;

    private Map<String, Object>       credit_score;                                 // 手机号和身份证信用评分
    private List<OutputField>         output_fields;                                // 策略结果自定义输出

    private JSONObject                credit_cloud;                                 // 信贷专用: 原来的Kafka消息内容

    /**
     * 测试调用记录执行和命中的规则
     */
    private List<String>              executedRuleIds;
    private List<String>              hitRuleIds;

    private List<Map<String, Object>> output_services;                              // 纯数据输出-决策流三方接口输出变量
    private List<Map<String, Object>> output_models;                                // 纯数据输出-决策流模型输出变量
    private Map<String, Double>       output_indicatrixes;                          // 纯数据输出-指标


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("success:").append(success);
        sb.append(",seqId:").append(seq_id);
        sb.append(",result:").append(final_decision);
        sb.append(",reasonCode:").append(reason_code);
        sb.append(",costTime:").append(spend_time);
        return sb.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
