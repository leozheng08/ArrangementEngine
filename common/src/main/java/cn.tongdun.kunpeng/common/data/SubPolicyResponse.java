package cn.tongdun.kunpeng.common.data;

import cn.tongdun.kunpeng.client.data.HitRule;
import cn.tongdun.kunpeng.client.data.PolicyMode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午4:44
 */
@Data
public class SubPolicyResponse extends Response{

    private String subPolicyUuid;
    private String subPolicyName;
    private String policyUuid;
    private String policyName;
    private String riskType;
    private PolicyMode policyMode;

    private boolean hit;

    private List<RuleResponse> ruleResponses = new ArrayList<>();
    private List<RuleResponse> hitRules = new ArrayList<>();

//    private List<HitRule> hitRules = new ArrayList<>();

    public void addRuleResponse(RuleResponse ruleResponse){
        if(ruleResponse == null){
            return;
        }
        ruleResponses.add(ruleResponse);
        if(ruleResponse.isHit()){
            hitRules.add(ruleResponse);
        }
    }



//    public void addHitRule(HitRule hitRule){
//        hitRules.add(hitRule);
//    }



}
