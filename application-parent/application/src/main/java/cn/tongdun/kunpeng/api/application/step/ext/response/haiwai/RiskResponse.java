package cn.tongdun.kunpeng.api.application.step.ext.response.haiwai;

import cn.tongdun.kunpeng.api.ruledetail.RuleDetail;
import cn.tongdun.kunpeng.client.data.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;

/**
 * 风险扫描返回结果
 * 
 * @author zxb 2014年3月12日 上午11:00:59
 */

public class RiskResponse extends ApiResponse implements IRiskResponse {

    private static final long         serialVersionUID    = 844958112006659504L;
    private Integer                   finalScore;                                  // 风险分数
    private String                    finalDecision;                               // 最终的风险决策结果
    private String                    policyName;                                  // 策略名称
    private String                    subPolicyName;                               // 子策略名称
    private List<IHitRule>            hitRules;                                    // 命中规则列表
    private String                    seqId;                                       // 请求序列号，每个请求进来都分配一个全局唯一的id
    private Integer                   spendTime;                                   // 花费的时间，单位ms

    private List<ISubPolicyResult>    subPolicys;                                  // 策略集信息

    private String                    riskType;                                    // 风险类型
    @JSONField(serialize = false, deserialize = false)
    private DecisionType decisionType        = DecisionType.POLICY_SET;


    private List<IOutputField>        outputFields;                                // 策略结果自定义输出


    @JSONField(serialize = false, deserialize = false)
    private List<RuleDetail>          ruleDetails;                                  //原始详情

    private String                    subReasonCodes;                              //原因子码


    @JSONField(serialize = false, deserialize = false)
    private IRiskResponseFactory factory;


    @Override
    @JSONField(name="final_score")
    public Integer getFinalScore() {
        return finalScore;
    }


    @Override
    @JSONField(name="final_score")
    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
    }

    @Override
    @JSONField(name="final_decision")
    public String getFinalDecision() {
        return finalDecision;
    }

    @Override
    @JSONField(name="final_decision")
    public void setFinalDecision(String finalDecision) {
        this.finalDecision = finalDecision;
    }

    @Override
    @JSONField(name="policy_set_name")
    public String getPolicyName() {
        return policyName;
    }

    @Override
    @JSONField(name="policy_set_name")
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }


    @JSONField(name="policy_name")
    public String getSubPolicyName() {
        return subPolicyName;
    }


    @JSONField(name="policy_name")
    public void setSubPolicyName(String subPolicyName) {
        this.subPolicyName = subPolicyName;
    }


    @JSONField(name="hit_rules")
    public List<IHitRule> getHitRules() {
        return hitRules;
    }

    @JSONField(name="hit_rules")
    public void setHitRules(List<IHitRule> hitRules) {
        this.hitRules = hitRules;
    }

    @Override
    @JSONField(name="seq_id")
    public String getSeqId() {
        return seqId;
    }

    @Override
    @JSONField(name="seq_id")
    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }

    @Override
    @JSONField(name="spend_time")
    public Integer getSpendTime() {
        return spendTime;
    }

    @Override
    @JSONField(name="spend_time")
    public void setSpendTime(Integer spendTime) {
        this.spendTime = spendTime;
    }

    @Override
    @JSONField(name="policy_set")
    public List<ISubPolicyResult> getSubPolicys() {
        return subPolicys;
    }

    @Override
    @JSONField(name="policy_set")
    public void setSubPolicys(List<ISubPolicyResult> subPolicys) {
        this.subPolicys = subPolicys;
    }

    @JSONField(name="risk_type")
    public String getRiskType() {
        return riskType;
    }


    @JSONField(name="risk_type")
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
    @JSONField(name="output_fields")
    public List<IOutputField> getOutputFields() {
        return outputFields;
    }

    @Override
    @JSONField(name="output_fields")
    public void setOutputFields(List<IOutputField> outputFields) {
        this.outputFields = outputFields;
    }

    @Override
    public List<RuleDetail> getRuleDetails() {
        return ruleDetails;
    }

    @Override
    public void setRuleDetails(List<RuleDetail> ruleDetail) {
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
        return JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
    }

    @Override
    public IRiskResponseFactory getFactory(){
        return factory;
    }


    public void setFactory(IRiskResponseFactory factory){
        this.factory = factory;
    }
}
