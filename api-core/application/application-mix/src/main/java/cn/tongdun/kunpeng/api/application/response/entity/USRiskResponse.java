package cn.tongdun.kunpeng.api.application.response.entity;

import cn.tongdun.kunpeng.api.ruledetail.RuleDetail;
import cn.tongdun.kunpeng.client.data.*;
import cn.tongdun.kunpeng.client.data.impl.us.PolicyResult;
import cn.tongdun.kunpeng.share.json.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

/**
 * @author: yuanhang
 * @date: 2020-06-17 12:00
 **/
public class USRiskResponse implements IRiskResponse {

    /**
     * 兼容保留字段， 默认为false
     */
    private Boolean success = false;

    private Boolean ignoreReq;

    /**
     * 用户自定义输出
     */
    private Map customPolicyResult;

    private PolicyResult policyDetailResult;

    @JsonIgnore
    private USRiskResponseFactory factory;
    /**
     * 风险分数
     */
    @JsonIgnore
    private Integer finalScore;
    /**
     * 最终的风险决策结果
     */
    @JsonIgnore
    private String finalDecision;
    /**
     * 策略名称
     */
    @JsonIgnore
    private String policyName;
    /**
     * 子策略名称
     */
    @JsonIgnore
    private String subPolicyName;

    /**
     * 子策略掩码
     */
    @JsonIgnore
    private String subReasonCodes;
    /**
     * 命中规则列表
     */
    @JsonIgnore
    private List<IHitRule> hitRules;
    /**
     * 请求序列号，每个请求进来都分配一个全局唯一的id
     */
    @JsonIgnore
    private String seqId;
    /**
     * 花费的时间，单位ms
     */
    @JsonIgnore
    private Integer spendTime;
    /**
     * 策略集信息
     */
    @JsonIgnore
    private List<ISubPolicyResult> subPolicys;
    /**
     * 风险类型
     */
    @JsonIgnore
    private String riskType;
    /**
     * 规则详情
     */
    @JsonIgnore
    private List<RuleDetail> ruleDetails;

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }

    @Override
    public String getSubReasonCodes() {
        return this.subReasonCodes;
    }

    @Override
    public void setSubReasonCodes(String subReasonCodes) {
        this.subReasonCodes = subReasonCodes;
    }

    @Override
    public Integer getFinalScore() {
        return this.finalScore;
    }

    @Override
    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
    }

    @Override
    public String getFinalDecision() {
        return this.finalDecision;
    }

    @Override
    public void setFinalDecision(String finalDecision) {
        this.finalDecision = finalDecision;
    }

    @Override
    public String getPolicyName() {
        return this.policyName;
    }

    @Override
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    @Override
    public String getSeqId() {
        return this.seqId;
    }

    @Override
    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }

    @Override
    public Integer getSpendTime() {
        return this.spendTime;
    }

    @Override
    public void setSpendTime(Integer spendTime) {
        this.spendTime = spendTime;
    }

    @Override
    public List<ISubPolicyResult> getSubPolicys() {
        return this.subPolicys;
    }

    @Override
    public void setSubPolicys(List<ISubPolicyResult> subPolicys) {
        this.subPolicys = subPolicys;
    }

    @Override
    public List<RuleDetail> getRuleDetails() {
        return this.ruleDetails;
    }

    @Override
    public void setRuleDetails(List<RuleDetail> ruleDetail) {
        this.ruleDetails = ruleDetail;
    }

    @Override
    public List<IOutputField> getOutputFields() {
        return null;
    }

    @Override
    public void setOutputFields(List<IOutputField> outputFields) {

    }

    public void setFactory(USRiskResponseFactory factory) {
        this.factory = factory;
    }

    @Override
    public IRiskResponseFactory getFactory() {
        return this.factory;
    }

    @Override
    public boolean isSuccess() {
        return this.success;
    }

    @Override
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String getReasonCode() {
        return null;
    }

    @Override
    public void setReasonCode(String reasonCode) {

    }

    @Override
    public void setIgnoreReq(Boolean ignoreReq) {
        this.ignoreReq = ignoreReq;
    }

    @Override
    public Boolean getIgnoreReq() {
        return this.ignoreReq;
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
    public void setPolicyDetailResult(Object policyDetailResult) {
        this.policyDetailResult = (PolicyResult) policyDetailResult;
    }

    @Override
    public Object getPolicyDetailResult() {
        return this.policyDetailResult;
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

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }
}
