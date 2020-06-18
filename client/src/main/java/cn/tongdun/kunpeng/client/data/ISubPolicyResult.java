package cn.tongdun.kunpeng.client.data;

import java.io.Serializable;
import java.util.List;

public interface ISubPolicyResult extends Serializable {

    String getSubPolicyUuid() ;

    void setSubPolicyUuid(String subPolicyUuid) ;

    String getSubPolicyName() ;

    void setSubPolicyName(String subPolicyName) ;

    String getPolicyDecision() ;
    void setPolicyDecision(String policyDecision) ;

    int getPolicyScore() ;

    void setPolicyScore(int policyScore) ;

    String getRiskType() ;
    void setRiskType(String riskType) ;

    List<IHitRule> getHitRules() ;

    void setHitRules(List<IHitRule> hitRules);

    PolicyMode getPolicyMode() ;

    void setPolicyMode(PolicyMode policyMode) ;

    String getDealType();

    void setDealType(String dealType);

}
