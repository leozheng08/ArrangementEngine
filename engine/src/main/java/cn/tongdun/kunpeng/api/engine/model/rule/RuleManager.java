package cn.tongdun.kunpeng.api.engine.model.rule;

import cn.fraudmetrix.module.tdrule.rule.RuleResult;
import cn.tongdun.kunpeng.api.engine.IExecutor;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.RuleResponse;
import cn.tongdun.kunpeng.common.data.ReasonCode;
import cn.tongdun.kunpeng.common.data.SubReasonCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 规则执行，根据ruleUuid从缓存中取得规则实体Rule对象后运行。
 * @Author: liang.chen
 * @Date: 2019/12/16 下午7:58
 */
@Component
public class RuleManager implements IExecutor<String,RuleResponse> {

    public final static Number NaN = Double.NaN;

    @Autowired
    RuleCache ruleCache;

    @Override
    public RuleResponse execute(String uuid, AbstractFraudContext context){
        RuleResponse ruleResponse = new RuleResponse();

        Rule rule = ruleCache.get(uuid);
        if(rule == null || rule.getEval() == null){
            context.addSubReasonCode(new SubReasonCode(ReasonCode.RULE_NOT_FIND.getCode(), ReasonCode.RULE_NOT_FIND.getDescription(), "决策引擎执行"));
        }

        RuleResult ruleResult = rule.getEval().eval(context);

        switch (ruleResult.getEvalResult()){
            case True:
                ruleResponse.setHit(true);
                ruleResponse.setDecision(rule.getDecision());
                ruleResponse.setScore(getWeight(rule,context));
                break;
            case False:
                ruleResponse.setHit(false);
                break;
            case Terminate:
                ruleResponse.setTerminate(true);
                break;
            case Exception:
            case Unknown:
                context.addSubReasonCode(new SubReasonCode(ReasonCode.RULE_RUN_EXCEPTION.getCode(), ReasonCode.RULE_RUN_EXCEPTION.getDescription(), "决策引擎执行"));
                break;
        }

        ruleResponse.setId(rule.getRuleCustomId() != null? rule.getRuleCustomId():rule.getRuleId());
        ruleResponse.setName(rule.getName());
        ruleResponse.setUuid(rule.getUuid());
        ruleResponse.setParentUuid(rule.getParentUuid());
        ruleResponse.setCostTime(ruleResult.getCost());

        return ruleResponse;
    }

    private Integer getWeight(Rule rule,AbstractFraudContext context){
        Integer weight = 0;
        if( rule.getWeightEval() != null){
            Number n = rule.getWeightEval().eval(context);
            if(NaN.equals(n)){
                return weight;
            }
            weight = n.intValue();
        }
        return weight;
    }

    //命中后action操作
    private void action(Rule rule,RuleResult ruleResult,AbstractFraudContext context,RuleResponse ruleResponse){

    }
}
