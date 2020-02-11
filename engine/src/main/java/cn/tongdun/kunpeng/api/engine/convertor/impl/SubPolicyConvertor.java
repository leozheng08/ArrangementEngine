package cn.tongdun.kunpeng.api.engine.convertor.impl;

import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.dto.RuleDTO;
import cn.tongdun.kunpeng.api.engine.dto.SubPolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.common.data.DecisionResultType;
import cn.tongdun.kunpeng.common.data.PolicyMode;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
public class SubPolicyConvertor implements IConvertor<SubPolicyDTO,SubPolicy> {

    @Autowired
    DefaultConvertorFactory convertorFactory;

    @PostConstruct
    public void init(){
        convertorFactory.register(SubPolicyDTO.class,this);
    }

    @Override
    public SubPolicy convert(SubPolicyDTO t){
        SubPolicy subPolicy = new SubPolicy();
        subPolicy.setPolicyUuid(t.getPolicyUuid());
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
        List<RuleDTO> ruleDOList= t.getRules();
        if(ruleDOList != null){
            for(RuleDTO ruleDO:ruleDOList){
                ruleUuidList.add(ruleDO.getUuid());
            }
        }

        return subPolicy;
    }

    //决策结果，如Accept、Review、Reject, 但不限这三个结果，以后可能自定义;
    private void addDecisionResultType(SubPolicy subPolicy,SubPolicyDTO t){


        /**
         * 扩展字段，json结构
         *
         * mode=Weighted 才有下面的风险阈值配置
         * {
         * "riskThreshold":[
         *     {
         *         "start":0,
         *         "end":10,
         *         "riskDecision":"Accept"
         *     },
         *     {
         *         "start":10,
         *         "end":50,
         *         "riskDecision":"Review"
         *     },
         *     {
         *         "start":50,
         *         "end":100,
         *         "riskDecision":"Reject"
         *     }
         * ]
         * }
         */
        String attribute = t.getAttribute();
        if( attribute == null){
            return;
        }

        try {
            JSONObject json = JSONObject.parseObject(attribute);
            JSONArray riskThresholdArray = json.getJSONArray("riskThreshold");

            if(riskThresholdArray == null){
                return;
            }

            for(Object riskThresholdTmp:riskThresholdArray){
                JSONObject riskThreshold = (JSONObject)riskThresholdTmp;
                int start = riskThreshold.getInteger("start");
                int end = riskThreshold.getInteger("end");
                String riskDecision = riskThreshold.getString("riskDecision");

                DecisionResultType decisionResultType = new DecisionResultType(riskDecision,riskDecision);
                decisionResultType.setStartThreshold(start);
                decisionResultType.setEndThreshold(end);
                subPolicy.addDecisionResultType(riskDecision,decisionResultType);
            }

        } catch (Exception e){

        }

//        DecisionResultType accept = new DecisionResultType("Accept","通过");
//        accept.setStartThreshold(t.getBeginThreshold() != null? t.getBeginThreshold(): Integer.MIN_VALUE);
//        accept.setEndThreshold(t.getReviewThreshold());
//        subPolicy.addDecisionResultType(accept.getCode(),accept);
//
//        DecisionResultType review = new DecisionResultType("Review","Review");
//        review.setStartThreshold(t.getReviewThreshold());
//        review.setEndThreshold(t.getDenyThreshold());
//        subPolicy.addDecisionResultType(accept.getCode(),accept);
//
//        DecisionResultType reject = new DecisionResultType("Reject","拒绝");
//        reject.setStartThreshold(t.getDenyThreshold());
//        reject.setEndThreshold(t.getEndThreshold() != null? t.getEndThreshold():Integer.MAX_VALUE);
//        subPolicy.addDecisionResultType(accept.getCode(),accept);
    }
}
