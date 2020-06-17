package cn.tongdun.kunpeng.client.data.impl.us;

import cn.tongdun.kunpeng.client.data.ISubPolicyResult;
import cn.tongdun.kunpeng.client.data.impl.camel.HitRule;

import java.util.List;
import java.util.Map;

/**
 * @author: yuanhang
 * @date: 2020-06-17 12:03
 **/
public class PolicyResult {

    public boolean success;

    public boolean finalScore;

    public boolean policyName;

    public List<HitRule> hitRules;

    public String seqId;

    public long spendTime;

    public List<ISubPolicyResult> policySet;

    public String policySetName;

    public List<String> riskType;

    public String flowChargeSuccessed;

    public String emergencySwithcOn;

    public String finalDealType;

    public String finalDealTypeName;

    public String finalDealTypeGrade;

    public Map nusspecialMap;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isFinalScore() {
        return finalScore;
    }

    public void setFinalScore(boolean finalScore) {
        this.finalScore = finalScore;
    }

    public boolean isPolicyName() {
        return policyName;
    }

    public void setPolicyName(boolean policyName) {
        this.policyName = policyName;
    }

    public List<HitRule> getHitRules() {
        return hitRules;
    }

    public void setHitRules(List<HitRule> hitRules) {
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

    public String getFlowChargeSuccessed() {
        return flowChargeSuccessed;
    }

    public void setFlowChargeSuccessed(String flowChargeSuccessed) {
        this.flowChargeSuccessed = flowChargeSuccessed;
    }

    public String getEmergencySwithcOn() {
        return emergencySwithcOn;
    }

    public void setEmergencySwithcOn(String emergencySwithcOn) {
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
}
