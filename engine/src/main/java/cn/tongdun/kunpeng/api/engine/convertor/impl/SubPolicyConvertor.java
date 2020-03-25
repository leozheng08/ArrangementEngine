package cn.tongdun.kunpeng.api.engine.convertor.impl;

import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.dto.SubPolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultThreshold;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.client.data.PolicyMode;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultType;
import cn.tongdun.kunpeng.common.util.JsonUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 上午10:16
 */
@Component
@DependsOn(value = "defaultConvertorFactory")
public class SubPolicyConvertor implements IConvertor<SubPolicyDTO,SubPolicy> {

    @Autowired
    private DefaultConvertorFactory convertorFactory;

    @Autowired
    private DecisionResultTypeCache decisionResultTypeCache;

    @PostConstruct
    public void init(){
        convertorFactory.register(SubPolicyDTO.class,this);
    }

    @Override
    public SubPolicy convert(SubPolicyDTO t){
        SubPolicy subPolicy = new SubPolicy();
        subPolicy.setPolicyUuid(t.getPolicyUuid());
        subPolicy.setUuid(t.getUuid());
        subPolicy.setName(t.getName());
        subPolicy.setRiskType(t.getRiskType());
        subPolicy.setGmtModify(t.getGmtModify());

        //策略模式,如首次匹配、最坏匹配、权重模式
        PolicyMode policyMode = PolicyMode.Weighted;
        policyMode = PolicyMode.valueOf(t.getMode());
        subPolicy.setPolicyMode(policyMode);

        //决策结果，如Accept、Review、Reject
        addDecisionResultType(subPolicy,t);

        //规则列表
        subPolicy.setRuleUuidList(t.getRuleUuidList());

        return subPolicy;
    }

    //决策结果，如Accept、Review、Reject, 但不限这三个结果，以后可能自定义;
    private void addDecisionResultType(SubPolicy subPolicy,SubPolicyDTO t){
        /**
         * 扩展字段，json结构
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
        if(StringUtils.isBlank(attribute)){
            return;
        }

        Map json = JSON.parseObject(attribute,HashMap.class);
        String riskThresholdsStr = JsonUtil.getString(json,"riskThresholds");
        if(StringUtils.isBlank(riskThresholdsStr)){
            return;
        }
        List<HashMap> riskThresholdArray = JSON.parseArray  (riskThresholdsStr,HashMap.class);

        if(riskThresholdArray == null){
            return;
        }

        List<DecisionResultThreshold> riskThresholds = new ArrayList<>();
        for(Map riskThreshold:riskThresholdArray){
            int start = JsonUtil.getInteger(riskThreshold,"start");
            int end = JsonUtil.getInteger(riskThreshold,"end");
            String riskDecision = JsonUtil.getString(riskThreshold,"riskDecision");

            DecisionResultType decisionResultType = decisionResultTypeCache.get(riskDecision);

            DecisionResultThreshold decisionResultThreshold = new DecisionResultThreshold();
            decisionResultThreshold.setDecisionResultType(decisionResultType);
            decisionResultThreshold.setStartThreshold(start);
            decisionResultThreshold.setEndThreshold(end);
            riskThresholds.add(decisionResultThreshold);
        }

        //按分数从小到大排序
        Collections.sort(riskThresholds, new Comparator<DecisionResultThreshold>() {
            @Override
            public int compare(DecisionResultThreshold threshold1, DecisionResultThreshold threshold2) {
                return threshold1.getStartThreshold() - threshold2.getStartThreshold();
            }
        });
        subPolicy.setRiskThresholds(riskThresholds);
    }
}
