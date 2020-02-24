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
    private List<PolicyResult>        policy_set;                                   // 策略集信息
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

    public List<String> getExecutedRuleIds() {
        return executedRuleIds;
    }

    public void setExecutedRuleIds(List<String> executedRuleIds) {
        this.executedRuleIds = executedRuleIds;
    }

    public List<String> getHitRuleIds() {
        return hitRuleIds;
    }

    public void setHitRuleIds(List<String> hitRuleIds) {
        this.hitRuleIds = hitRuleIds;
    }

    public Boolean getEmergencySwithcOn() {
        return emergencySwithcOn;
    }

    public void setEmergencySwithcOn(Boolean emergencySwithcOn) {
        this.emergencySwithcOn = emergencySwithcOn;
    }

    public Boolean getFlowChargeSuccessed() {
        return flowChargeSuccessed;
    }

    public void setFlowChargeSuccessed(Boolean flowChargeSuccessed) {
        this.flowChargeSuccessed = flowChargeSuccessed;
    }



    public Integer getFinal_score() {
        return final_score;
    }

    public void setFinal_score(Integer risk_score) {
        this.final_score = risk_score;
    }

    public String getFinal_decision() {
        return final_decision;
    }

    public void setFinal_decision(String result) {
        this.final_decision = result;
    }

    public List<HitRule> getHit_rules() {
        return hit_rules;
    }

    public void setHit_rules(List<HitRule> hit_rules) {
        this.hit_rules = hit_rules;
    }

    public String getSeq_id() {
        return seq_id;
    }

    public void setSeq_id(String seq_id) {
        this.seq_id = seq_id;
    }

    public Integer getSpend_time() {
        return spend_time;
    }

    public void setSpend_time(Integer spend_time) {
        this.spend_time = spend_time;
    }

    public String getPolicy_name() {
        return policy_name;
    }

    public void setPolicy_name(String policy_name) {
        this.policy_name = policy_name;
    }

    public Map<String, String> getGeoip_info() {
        return geoip_info;
    }

    public void setGeoip_info(Map<String, String> geoip_info) {
        this.geoip_info = geoip_info;
    }

    public Map<String, Object> getDevice_info() {
        return device_info;
    }

    public void setDevice_info(Map<String, Object> device_info) {
        this.device_info = device_info;
    }

    public Map<String, String> getAttribution() {
        return attribution;
    }

    public void setAttribution(Map<String, String> attribution) {
        this.attribution = attribution;
    }

    public List<PolicyResult> getPolicy_set() {
        return policy_set;
    }

    public void setPolicy_set(List<PolicyResult> policy_set) {
        this.policy_set = policy_set;
    }

    public String getPolicy_set_name() {
        return policy_set_name;
    }

    public void setPolicy_set_name(String policy_set_name) {
        this.policy_set_name = policy_set_name;
    }

    public String getRisk_type() {
        return risk_type;
    }

    public void setRisk_type(String risk_type) {
        this.risk_type = risk_type;
    }


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

    public void setHit_rule_detail_info(Map<String, Object> ruleDetail) {
        this.hitRuleDetail_info = ruleDetail;
    }

    public Map<String, Object> getHit_rule_detail_info() {
        return hitRuleDetail_info;
    }

    public Map<String, Object> getCredit_score() {
        return credit_score;
    }

    public void setCredit_score(Map<String, Object> credit_score) {
        this.credit_score = credit_score;
    }

    public String getApplication_id() {
        return application_id;
    }

    public void setApplication_id(String application_id) {
        this.application_id = application_id;
    }

    public Map<String, Object> getHit_rule_detail_v3_info() {
        return hit_rule_detail_v3_info;
    }

    public void setHit_rule_detail_v3_info(Map<String, Object> hit_rule_detail_v3_info) {
        this.hit_rule_detail_v3_info = hit_rule_detail_v3_info;
    }

    public boolean isContainApplicationId() {
        return isContainApplicationId;
    }

    public void setContainApplicationId(boolean containApplicationId) {
        isContainApplicationId = containApplicationId;
    }

    public List<OutputField> getOutput_fields() {
        return output_fields;
    }

    public void setOutput_fields(List<OutputField> output_fields) {
        this.output_fields = output_fields;
    }

    public JSONObject getCredit_cloud() {
        return credit_cloud;
    }

    public void setCredit_cloud(JSONObject credit_cloud) {
        this.credit_cloud = credit_cloud;
    }

    public DecisionType getDecisionType() {
        return decisionType == null ? DecisionType.POLICY_SET : decisionType;
    }

    public void setDecisionType(DecisionType decisionType) {
        this.decisionType = decisionType;
    }

    public List<Map<String, Object>> getOutput_services() {
        return output_services;
    }

    public void setOutput_services(List<Map<String, Object>> output_services) {
        this.output_services = output_services;
    }

    public Map<String, Double> getOutput_indicatrixes() {
        return output_indicatrixes;
    }

    public void setOutput_indicatrixes(Map<String, Double> output_indicatrixes) {
        this.output_indicatrixes = output_indicatrixes;
    }

    public List<Map<String, Object>> getOutput_models() {
        return output_models;
    }

    public void setOutput_models(List<Map<String, Object>> output_models) {
        this.output_models = output_models;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
