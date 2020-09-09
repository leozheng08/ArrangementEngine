package cn.tongdun.kunpeng.client.data.impl.underline;

import cn.tongdun.kunpeng.api.ruledetail.RuleDetail;
import cn.tongdun.kunpeng.client.data.*;
import cn.tongdun.kunpeng.share.json.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * 风险扫描返回结果
 * 
 * @author zxb 2014年3月12日 上午11:00:59
 */

public class RiskResponse extends ApiResponse implements IRiskResponse {

    private static final long         serialVersionUID    = 844958112006659504L;
    // 风险分数
    @JsonProperty("final_score")
    private Integer                   finalScore;
    // 最终的风险决策结果
    @JsonProperty("final_decision")
    private String                    finalDecision;
    // 策略名称
    @JsonProperty("policy_set_name")
    private String                    policyName;
    // 子策略名称
    @JsonProperty("policy_name")
    private String                    subPolicyName;
    // 命中规则列表
    @JsonProperty("hit_rules")
    private List<IHitRule>            hitRules;
    // 请求序列号，每个请求进来都分配一个全局唯一的id
    @JsonProperty("seq_id")
    private String                    seqId;
    // 花费的时间，单位ms
    @JsonProperty("spend_time")
    private Integer                   spendTime;
    // 策略集信息
    @JsonProperty("policy_set")
    private List<ISubPolicyResult>    subPolicys;
    // 风险类型
    @JsonProperty("risk_type")
    private String                    riskType;

    @JsonProperty("policy_uuid")
    private String                    policyUuid;

    @JsonIgnore
    private DecisionType decisionType        = DecisionType.POLICY_SET;

    // 策略结果自定义输出
    @JsonProperty("output_fields")
    private List<IOutputField>        outputFields;

    //原始详情
    @JsonIgnore
    private List<RuleDetail>          ruleDetails;

    //原因子码
    @JsonIgnore
    private String                    subReasonCodes;

    /**
     * 用户自定义输出
     */
    private Map customPolicyResult;

    @JsonIgnore
    private transient IRiskResponseFactory      factory;


    @Override
    public Integer getFinalScore() {
        return finalScore;
    }


    @Override
    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
    }

    @Override
    public String getFinalDecision() {
        return finalDecision;
    }

    @Override
    public void setFinalDecision(String finalDecision) {
        this.finalDecision = finalDecision;
    }

    @Override
    public String getPolicyName() {
        return policyName;
    }

    @Override
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getSubPolicyName() {
        return subPolicyName;
    }

    public void setSubPolicyName(String subPolicyName) {
        this.subPolicyName = subPolicyName;
    }


    public List<IHitRule> getHitRules() {
        return hitRules;
    }

    public void setHitRules(List<IHitRule> hitRules) {
        this.hitRules = hitRules;
    }

    @Override
    public String getSeqId() {
        return seqId;
    }

    @Override
    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }

    @Override
    public Integer getSpendTime() {
        return spendTime;
    }

    @Override
    public void setSpendTime(Integer spendTime) {
        this.spendTime = spendTime;
    }

    @Override
    public List<ISubPolicyResult> getSubPolicys() {
        return subPolicys;
    }

    @Override
    public void setSubPolicys(List<ISubPolicyResult> subPolicys) {
        this.subPolicys = subPolicys;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }


    public DecisionType getDecisionType() {
        return decisionType;
    }


    public void setDecisionType(DecisionType decisionType) {
        this.decisionType = decisionType;
    }

    @Override
    public List<IOutputField> getOutputFields() {
        return outputFields;
    }

    @Override
    public void setOutputFields(List<IOutputField> outputFields) {
        this.outputFields = outputFields;
    }

    @Override
    public List<RuleDetail> getRuleDetails() {
        return ruleDetails;
    }

    @Override
    public void setRuleDetails(List<RuleDetail> ruleDetails) {
        this.ruleDetails = ruleDetails;
    }

    @Override
    public String getSubReasonCodes() {
        return subReasonCodes;
    }

    @Override
    public void setSubReasonCodes(String subReasonCodes) {
        this.subReasonCodes = subReasonCodes;
    }

    /**
     * 输出为json
     * @return
     */
    @Override
    public String toJsonString(){
        return JSON.toJSONString(this);
    }

    @Override
    public IRiskResponseFactory getFactory(){
        return factory;
    }

    @Override
    public void setCustomPolicyResult(Map customPolicyResult) {
        this.customPolicyResult = customPolicyResult;
    }

    @Override
    public Map getCustomPolicyResult() {
        return this.customPolicyResult;
    }

    @Override
    public void setIgnoreReq(Boolean ignoreReq) {

    }

    @Override
    public Boolean getIgnoreReq() {
        return null;
    }

    @Override
    public void setPolicyDetailResult(Object policyDetailResult) {

    }

    @Override
    public List getPolicyDetailResult() {
        return null;
    }

    @Override
    public String getPolicyUuid() {
        return policyUuid;
    }

    @Override
    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }


    public void setFactory(IRiskResponseFactory factory){
        this.factory = factory;
    }
}
