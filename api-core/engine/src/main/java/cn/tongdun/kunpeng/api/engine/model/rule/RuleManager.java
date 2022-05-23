package cn.tongdun.kunpeng.api.engine.model.rule;

import cn.fraudmetrix.module.tdrule.rule.RuleResult;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.data.RuleResponse;
import cn.tongdun.kunpeng.api.common.data.SubReasonCode;
import cn.tongdun.kunpeng.api.engine.IExecutor;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger logger = LoggerFactory.getLogger(RuleManager.class);

    @Autowired
    RuleCache ruleCache;

    @Override
    public RuleResponse execute(String uuid, AbstractFraudContext context) {
        try {
            RuleResponse ruleResponse = new RuleResponse();

            Rule rule = ruleCache.get(uuid);

            // 试运行的规则不显示错误状态码
            if ((rule == null || rule.getEval() == null) && !rule.isPilotRun()) {
                context.addSubReasonCode(new SubReasonCode(ReasonCode.RULE_LOAD_ERROR.getCode(), ReasonCode.RULE_LOAD_ERROR.getDescription(), "决策引擎执行"));
                ruleResponse.setSuccess(false);
                return ruleResponse;
            }

            RuleResult ruleResult = rule.getEval().eval(context);

            // 试运行的规则不显示错误状态码
            if ((null == ruleResult || ruleResult.getException() != null || ruleResult.getEvalResult() == null) && !rule.isPilotRun()) {
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
                        if(!rule.isPilotRun()){
                            break;
                        }
                    default:
                        // 试运行的规则不显示错误状态码
                        if (!rule.isPilotRun()) {
                            context.addSubReasonCode(new SubReasonCode(ReasonCode.RULE_ENGINE_ERROR.getCode(), ReasonCode.RULE_ENGINE_ERROR.getDescription(), "决策引擎执行"));
                            context.removeFunctionDetail(uuid);
                        }
                }
                ruleResponse.setCostTime(ruleResult.getCost());
                ruleResponse.setSuccess(true);
            }

            ruleResponse.setId(rule.getRuleCustomId() != null ? rule.getRuleCustomId() : rule.getRuleId());
            ruleResponse.setName(rule.getName());
            ruleResponse.setUuid(rule.getUuid());
            ruleResponse.setParentUuid(rule.getParentUuid());
            // 规则结果返回一个试运行标识
            ruleResponse.setPilotRun(rule.isPilotRun());

            return ruleResponse;
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "RuleManager::execute error,uuid:" + uuid, e);
            throw e;
        }

    }

    private Integer getWeight(Rule rule, AbstractFraudContext context) {
        Integer weight = 0;
        if (rule.getWeightFunction() != null) {
            weight = (Integer) rule.getWeightFunction().eval(context);
        }
        return weight;
    }
}
