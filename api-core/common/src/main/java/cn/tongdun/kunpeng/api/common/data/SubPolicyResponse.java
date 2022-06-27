package cn.tongdun.kunpeng.api.common.data;

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

    /**
     * 正常业务数据：仅正常线上规则
     */
    private List<RuleResponse> ruleResponses;
    private List<RuleResponse> hitRules;

    /**
     * 试运行数据：是正常数据+试运行数据
     */
    private List<RuleResponse> tryRuleResponses;
    private List<RuleResponse> tryHitRules;

    /**
     * tryScore:试运行分数
     * tryDecision：试运行决策结果
     */
    private Integer tryScore = 0;
    private String tryDecision;


    public SubPolicyResponse(){
        ruleResponses = new ArrayList<>();
        hitRules = new ArrayList<>();
        tryRuleResponses = new ArrayList<>();
        tryHitRules = new ArrayList<>();
    }
    public SubPolicyResponse(int ruleCount) {
        ruleResponses = new ArrayList<>(ruleCount);
        tryRuleResponses = new ArrayList<>();
        //假设有20%的规则命中
        int hitCount = ruleCount / 5;
        if (hitCount < 3) {
            hitCount = 3;
        }
        hitRules = new ArrayList<>(hitCount);
        tryHitRules = new ArrayList<>();
    }

    public void addRuleResponse(RuleResponse ruleResponse){
        if(ruleResponse == null){
            return;
        }
        ruleResponses.add(ruleResponse);
        if(ruleResponse.isHit()){
            hitRules.add(ruleResponse);
        }
    }

    public void addTryRuleResponse(RuleResponse ruleResponse) {
        if (ruleResponse == null) {
            return;
        }
        tryRuleResponses.add(ruleResponse);
        if (ruleResponse.isHit()) {
            tryHitRules.add(ruleResponse);
        }
    }
}
