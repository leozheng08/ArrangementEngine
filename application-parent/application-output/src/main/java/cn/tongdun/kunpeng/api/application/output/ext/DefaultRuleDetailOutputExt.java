package cn.tongdun.kunpeng.api.application.output.ext;

import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.api.ruledetail.ConditionDetail;
import cn.tongdun.kunpeng.api.ruledetail.RuleDetail;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 详情输出扩展点默认实现
 * @Author: liang.chen
 * @Date: 2020/2/10 下午9:44
 */
@Extension(business = BizScenario.DEFAULT,tenant = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class DefaultRuleDetailOutputExt implements IRuleDetailOutputExtPt {


    @Autowired
    private DecisionResultTypeCache decisionResultTypeCache;

    @Override
    public boolean ruleDetailOutput(AbstractFraudContext context,IRiskResponse response){

        Map<String, Map<String, DetailCallable>> functionHitDetail = context.getFunctionHitDetail();
        if(functionHitDetail == null || functionHitDetail.isEmpty()){
            return true;
        }

        List<RuleDetail> ruleDetailList = new ArrayList<>();

        for(Map.Entry<String, Map<String, DetailCallable>> ruleEntry :functionHitDetail.entrySet()){
            RuleDetail ruleDetail = new RuleDetail();
            ruleDetail.setRuleUuid(ruleEntry.getKey());
            List<ConditionDetail> conditions = new ArrayList<>();
            for(Map.Entry<String, DetailCallable> conditionEntry: ruleEntry.getValue().entrySet()){
                conditions.add((ConditionDetail) conditionEntry.getValue().call());
            }
            if(!conditions.isEmpty()){
                ruleDetail.setConditions(conditions);
            }
            ruleDetailList.add(ruleDetail);
        }

        if(!ruleDetailList.isEmpty()) {
            response.setRawRuleDetail(ruleDetailList);
        }

        return true;
    }
}
