package cn.tongdun.kunpeng.client.data;

import cn.tongdun.kunpeng.api.ruledetail.RuleDetail;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/2/28 下午5:43
 */
public interface IRiskResponse extends IApiResponse {

    /**
     * 输出为json
     * @return
     */
    String toJsonString();

    String getSubReasonCodes();

    void setSubReasonCodes(String subReasonCodes);

    Integer getFinalScore() ;

    void setFinalScore(Integer finalScore) ;

    String getFinalDecision() ;

    void setFinalDecision(String finalDecision) ;

    String getPolicyName() ;

    void setPolicyName(String policyName) ;

    String getSeqId() ;

    void setSeqId(String seqId) ;
    Integer getSpendTime() ;

    void setSpendTime(Integer spendTime) ;

    List<ISubPolicyResult> getSubPolicys() ;

    void setSubPolicys(List<ISubPolicyResult> subPolicys) ;

//    List<IHitRule> getHitRules() ;
//
//    void setHitRules(List<IHitRule> hitRules) ;

    List<RuleDetail> getRuleDetails() ;
    void setRuleDetails(List<RuleDetail> ruleDetail) ;

    List<IOutputField> getOutputFields();
    void setOutputFields(List<IOutputField> outputFields) ;


    IRiskResponseFactory getFactory();

    void setCustomPolicyResult(Map customPolicyResult);

    Map getCustomPolicyResult();

    void setIgnoreReq(Boolean ignoreReq);

    Boolean getIgnoreReq();

    void setPolicyDetailResult(Object policyDetailResult);

    Object getPolicyDetailResult();

    String getPolicyUuid();

    void setPolicyUuid(String policyUuid);

    Map<String, String> getGeoipInfo();

    void setGeoipInfo(Map<String, String> geoipInfo);

    Map<String, Object> getDeviceInfo();

    void setDeviceInfo(Map<String, Object> deviceInfo);

    Map<String, String> getAttribution();

    void setAttribution(Map<String, String> attribution);

    Map<String, Double> getOutputIndicatrixes();

    List<Map<String, Object>> getOutputModels();

    void setOutputModels(List<Map<String, Object>> outputModels);

    List<Map<String, Object>> getOutputServices();

    void setOutput_services(List<Map<String, Object>> outputServices);

    void setOutputIndicatrixes(Map<String, Double> outputIndicatrixes);

    Map<String, Object> getCreditScore();

    void setCreditScore(Map<String, Object> creditScore);

    Map<String, Object> getHitRuleDetailInfo();

    void setHitRuleDetailInfo(Map<String, Object> hitRuleDetailInfo);

    Map<String, Object> getHitRuleDetailV3Info();

    void setHitRuleDetailV3Info(Map<String, Object> hitRuleDetailV3Info);

    String getRiskType();

    void setRiskType(String riskType);

    Boolean getFlowChargeSuccessed();

    void setFlowChargeSuccessed(Boolean flowChargeSuccessed);

    // boolean isContainApplicationId();

    // void setContainApplicationId(boolean containApplicationId);

    Boolean getEmergencySwithcOn();

    void setEmergencySwithcOn(Boolean emergencySwithcOn);

    List<String> getExecutedRuleIds();

    void setExecutedRuleIds(List<String> executedRuleIds);

    List<String> getHitRuleIds();

    void setHitRuleIds(List<String> hitRuleIds);

    DecisionType getDecisionType();

    void setDecisionType(DecisionType decisionType);

    String getSubPolicyName();

    void setSubPolicyName(String policyName);

    List<IHitRule> getHitRules();

    void setHitRules(List<IHitRule> hitRules);

    String getApplicationId();

    void setApplicationId(String applicationId);

    JSONObject getCreditCloud();

    void setCreditCloud(JSONObject creditCloud);

    List<RuleDetail> getRuleTestDetails();

    void setRuleTestDetails(List<RuleDetail> ruleTestDetails);
}
