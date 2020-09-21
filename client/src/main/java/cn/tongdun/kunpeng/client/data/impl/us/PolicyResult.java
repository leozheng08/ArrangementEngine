package cn.tongdun.kunpeng.client.data.impl.us;

import cn.tongdun.kunpeng.client.data.IHitRule;
import cn.tongdun.kunpeng.client.data.ISubPolicyResult;

import java.util.List;
import java.util.Map;

/**
 * @author: yuanhang
 * @date: 2020-06-17 12:03
 **/
public class PolicyResult {

    private boolean success;

    private Integer finalScore;

    private String policyName;

    private List<IHitRule> hitRules;

    private String reasonCode;

    private String seqId;

    private long spendTime;

    private List<ISubPolicyResult> policySet;

    private String policySetName;

    private List<String> riskType;

    private Boolean flowChargeSuccessed;

    private Boolean emergencySwithcOn;

    private String finalDealType;

    private String finalDealTypeName;

    private String finalDealTypeGrade;

    private String policyUuid;

    private Map nusspecialMap;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public List<IHitRule> getHitRules() {
        return hitRules;
    }

    public void setHitRules(List<IHitRule> hitRules) {
        this.hitRules = hitRules;
    }

    public String getSeqId() {
        return seqId;
    }

    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }

    public long getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(long spendTime) {
        this.spendTime = spendTime;
    }

    public List<ISubPolicyResult> getPolicySet() {
        return policySet;
    }

    public void setPolicySet(List<ISubPolicyResult> policySet) {
        this.policySet = policySet;
    }

    public String getPolicySetName() {
        return policySetName;
    }

    public void setPolicySetName(String policySetName) {
        this.policySetName = policySetName;
    }

    public List<String> getRiskType() {
        return riskType;
    }

    public void setRiskType(List<String> riskType) {
        this.riskType = riskType;
    }

    public Boolean isFlowChargeSuccessed() {
        return flowChargeSuccessed;
    }

    public void setFlowChargeSuccessed(Boolean flowChargeSuccessed) {
        this.flowChargeSuccessed = flowChargeSuccessed;
    }

    public Boolean isEmergencySwithcOn() {
        return emergencySwithcOn;
    }

    public void setEmergencySwithcOn(Boolean emergencySwithcOn) {
        this.emergencySwithcOn = emergencySwithcOn;
    }

    public String getFinalDealType() {
        return finalDealType;
    }

    public void setFinalDealType(String finalDealType) {
        this.finalDealType = finalDealType;
    }

    public String getFinalDealTypeName() {
        return finalDealTypeName;
    }

    public void setFinalDealTypeName(String finalDealTypeName) {
        this.finalDealTypeName = finalDealTypeName;
    }

    public String getFinalDealTypeGrade() {
        return finalDealTypeGrade;
    }

    public void setFinalDealTypeGrade(String finalDealTypeGrade) {
        this.finalDealTypeGrade = finalDealTypeGrade;
    }

    public Map getNusspecialMap() {
        return nusspecialMap;
    }

    public void setNusspecialMap(Map nusspecialMap) {
        this.nusspecialMap = nusspecialMap;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }
}
