package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import cn.fraudmetrix.module.tdflow.definition.EdgeDesc;
import cn.fraudmetrix.module.tdflow.exception.ParseException;
import cn.fraudmetrix.module.tdflow.model.edge.EdgeCondition;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.EvalResult;
import cn.fraudmetrix.module.tdrule.rule.RuleResult;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.tongdun.kunpeng.api.engine.convertor.rule.CustomRuleBuilder;
import cn.tongdun.kunpeng.api.engine.convertor.rule.RuleBuilder;
import cn.tongdun.kunpeng.api.engine.dto.RuleDTO;
import cn.tongdun.kunpeng.api.engine.model.rule.IRuleRepository;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: liuq
 * @Date: 2020/2/19 1:53 PM
 */
public class RuleEdgeCondition extends EdgeCondition {

    private static final RuleBuilder ruleBuilder = new CustomRuleBuilder();

    private Rule rule;

    @Override
    public void parse(EdgeDesc edgeDesc) {

        Map<String, String> paramMap = edgeDesc.getParamList().stream().collect(Collectors.toMap(a -> a.getName(), b -> b.getValue()));
        String ruleUuid = paramMap.get("tns:ruleUuid");
        if (StringUtils.isBlank(ruleUuid)) {
            throw new ParseException("Edge id:" + edgeDesc.getId() + " rule condition tns:taskName is blank or tns:ruleUuid is blank!");
        }
        IRuleRepository ruleRepository = (IRuleRepository) SpringContextHolder.getBean("ruleRepository");
        RuleDTO ruleDTO = ruleRepository.queryByUuid(ruleUuid);
        this.rule = ruleBuilder.build(ruleDTO);
    }

    @Override
    public Boolean eval(ExecuteContext executeContext) {
        RuleResult ruleResult = rule.getEval().eval(executeContext);
        if (ruleResult.getException() != null) {
            return false;
        }
        if (ruleResult.getEvalResult() == null) {
            return false;
        }
        return ruleResult.getEvalResult() == EvalResult.True;
    }
}
