package cn.tongdun.kunpeng.api.convertor.impl;

import cn.tongdun.kunpeng.api.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.convertor.IConvertor;
import cn.tongdun.kunpeng.api.dataobject.RuleDO;
import cn.tongdun.kunpeng.api.dataobject.SubPolicyDO;
import cn.tongdun.kunpeng.api.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.common.data.DecisionResultType;
import cn.tongdun.kunpeng.common.data.PolicyMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 上午10:16
 */
@Component
@DependsOn(value = "defaultConvertorFactory")
public class SubPolicyConvertor implements IConvertor<SubPolicyDO,SubPolicy> {

    @Autowired
    DefaultConvertorFactory convertorFactory;

    @PostConstruct
    public void init(){
        convertorFactory.register(SubPolicyDO.class,this);
    }

    @Override
    public SubPolicy convert(SubPolicyDO t){
        SubPolicy subPolicy = new SubPolicy();
        subPolicy.setPolicyUuid(t.getFkPolicySetUuid());
        subPolicy.setSubPolicyUuid(t.getUuid());
        subPolicy.setSubPolicyName(t.getName());
        subPolicy.setRiskType(t.getRiskType());

        //策略模式,如首次匹配、最坏匹配、权重模式
        PolicyMode policyMode = PolicyMode.Weighted;
        try{
            policyMode = PolicyMode.valueOf(t.getMode());
        }catch (Exception e){
        }
        subPolicy.setPolicyMode(policyMode);


        //决策结果，如Accept、Review、Reject
        addDecisionResultType(subPolicy,t);

        //规则列表
        List<String> ruleUuidList = new ArrayList<>();
        subPolicy.setRuleUuidList(ruleUuidList);
        List<RuleDO> ruleDOList= t.getRules();
        if(ruleDOList != null){
            for(RuleDO ruleDO:ruleDOList){
                ruleUuidList.add(ruleDO.getUuid());
            }
        }

        return subPolicy;
    }

    //决策结果，如Accept、Review、Reject, 但不限这三个结果，以后可能自定义;
    private void addDecisionResultType(SubPolicy subPolicy,SubPolicyDO t){


        DecisionResultType accept = new DecisionResultType("Accept","通过");
        accept.setStartThreshold(t.getBeginThreshold() != null? t.getBeginThreshold(): Integer.MIN_VALUE);
        accept.setEndThreshold(t.getReviewThreshold());
        subPolicy.addDecisionResultType(accept.getCode(),accept);

        DecisionResultType review = new DecisionResultType("Review","Review");
        review.setStartThreshold(t.getReviewThreshold());
        review.setEndThreshold(t.getDenyThreshold());
        subPolicy.addDecisionResultType(accept.getCode(),accept);

        DecisionResultType reject = new DecisionResultType("Reject","拒绝");
        reject.setStartThreshold(t.getDenyThreshold());
        reject.setEndThreshold(t.getEndThreshold() != null? t.getEndThreshold():Integer.MAX_VALUE);
        subPolicy.addDecisionResultType(accept.getCode(),accept);
    }
}
