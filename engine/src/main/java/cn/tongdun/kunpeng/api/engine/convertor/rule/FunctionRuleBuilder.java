package cn.tongdun.kunpeng.api.engine.convertor.rule;

import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.rule.Condition;
import cn.tongdun.kunpeng.api.engine.dto.RuleConditionElementDTO;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Author: liuq
 * @Date: 2020/2/14 11:22 AM
 */
public class FunctionRuleBuilder extends AbstractRuleBuilder {

    @Override
    protected void convertRuleCondtionElement2RawRule(List<RuleConditionElementDTO> ruleConditionElements, RawRule rawRule) {

        if (null == ruleConditionElements || ruleConditionElements.isEmpty() || null == rawRule) {
            return;
        }
        //函数规则只有1个RuleConditionElementDTO
        if (ruleConditionElements.size() != 1) {
            throw new ParseException("FunctionRuleBuilder formate error,first layer don't have one elemvent!");
        }

        List<Condition> conditionList = Lists.newArrayList();
        List<FunctionDesc> functionDescList = Lists.newArrayList();

        /**
         * 用于给函数计数，把condition和函数对应起来
         */
        Integer num = new Integer(1);
        processOneElement(ruleConditionElements.get(0), conditionList, functionDescList, num);
        rawRule.setConditionList(conditionList);
        rawRule.setFunctionDescList(functionDescList);
    }
}
