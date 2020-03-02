package cn.tongdun.kunpeng.client.data;

import java.io.Serializable;
import java.util.List;

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

    Integer getFinalScore() ;

    void setFinalScore(Integer finalScore) ;

    String getFinalDecision() ;

    void setFinalDecision(String finalDecision) ;

    String getPolicyName() ;

    void setPolicyName(String policyName) ;

    String getSubPolicyName() ;

    void setSubPolicyName(String subPolicyName) ;

    String getSeqId() ;

    void setSeqId(String seqId) ;
    Integer getSpendTime() ;

    void setSpendTime(Integer spendTime) ;
    String getRiskType() ;

    void setRiskType(String riskType) ;

    DecisionType getDecisionType() ;

    void setDecisionType(DecisionType decisionType) ;

    List<ISubPolicyResult> getSubPolicys() ;

    void setSubPolicys(List<ISubPolicyResult> subPolicys) ;

//    List<IHitRule> getHitRules() ;
//
//    void setHitRules(List<IHitRule> hitRules) ;

    List getRawRuleDetail() ;
    void setRawRuleDetail(List rawRuleDetail) ;

    List<IOutputField> getOutputFields();
    void setOutputFields(List<IOutputField> outputFields) ;

    IRiskResponseFactory getFactory();
}