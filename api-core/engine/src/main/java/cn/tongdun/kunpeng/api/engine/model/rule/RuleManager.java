package cn.tongdun.kunpeng.api.engine.model.rule;

import cn.fraudmetrix.module.tdrule.rule.RuleResult;
import cn.tongdun.kunpeng.api.engine.IExecutor;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.RuleResponse;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.data.SubReasonCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 规则执行，根据ruleUuid从缓存中取得规则实体Rule对象后运行。
 *
 * @Author: liang.chen
 * @Date: 2019/12/16 下午7:58
 */
@Component
public class RuleManager implements IExecutor<String, RuleResponse> {

    public final static Number NaN = Double.NaN;

    @Autowired
    RuleCache ruleCache;

    @Override
    public RuleResponse execute(String uuid, AbstractFraudContext context) {
        RuleResponse ruleResponse = new RuleResponse();

        Rule rule = ruleCache.get(uuid);
        if (rule == null || rule.getEval() == null) {
            context.addSubReasonCode(new SubReasonCode(ReasonCode.RULE_LOAD_ERROR.getCode(), ReasonCode.RULE_LOAD_ERROR.getDescription(), "决策引擎执行"));
            ruleResponse.setSuccess(false);
            return ruleResponse;
        }

        RuleResult ruleResult = rule.getEval().eval(context);
        if (null == ruleResult || ruleResult.getException() != null || ruleResult.getEvalResult() == null) {
            context.addSubReasonCode(new SubReasonCode(ReasonCode.RULE_ENGINE_ERROR.getCode(), ReasonCode.RULE_ENGINE_ERROR.getDescription(), "决策引擎执行"));
            context.removeFunctionDetail(uuid);
            ruleResponse.setSuccess(false);
        } else {
            switch (ruleResult.getEvalResult()) {
                case True:
                    ruleResponse.setHit(true);
                    ruleResponse.setDecision(rule.getDecision());
                    ruleResponse.setScore(getWeight(rule, context));
                    break;
                case False:
                    ruleResponse.setHit(false);
                    context.removeFunctionDetail(uuid);
                    break;
                case Terminate:
                    //subPolicy在执行时，如果某条规则返回Terminate=true，则不再执行后继规则。
                    ruleResponse.setTerminate(true);
                    context.removeFunctionDetail(uuid);
                    ruleResponse.setHit(true);
                    ruleResponse.setDecision(rule.getDecision());
                    ruleResponse.setScore(getWeight(rule, context));
                    break;
                default:
                    context.addSubReasonCode(new SubReasonCode(ReasonCode.RULE_ENGINE_ERROR.getCode(), ReasonCode.RULE_ENGINE_ERROR.getDescription(), "决策引擎执行"));
                    context.removeFunctionDetail(uuid);
            }
            ruleResponse.setCostTime(ruleResult.getCost());
            ruleResponse.setSuccess(true);
        }

        ruleResponse.setId(rule.getRuleCustomId() != null ? rule.getRuleCustomId() : rule.getRuleId());
        ruleResponse.setName(rule.getName());
        ruleResponse.setUuid(rule.getUuid());
        ruleResponse.setParentUuid(rule.getParentUuid());

        return ruleResponse;
    }

    private Integer getWeight(Rule rule, AbstractFraudContext context) {
        Integer weight = 0;
        if (rule.getWeightFunction() != null) {
            weight = (Integer) rule.getWeightFunction().eval(context);
        }
        return weight;
    }
}
