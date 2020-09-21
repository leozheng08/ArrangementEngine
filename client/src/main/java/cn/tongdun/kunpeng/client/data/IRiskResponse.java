package cn.tongdun.kunpeng.client.data;

import cn.tongdun.kunpeng.api.ruledetail.RuleDetail;

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
}
