package cn.tongdun.kunpeng.api.engine.convertor.rule;

import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.rule.Condition;
import cn.tongdun.kunpeng.api.engine.dto.RuleConditionElementDTO;
import cn.tongdun.kunpeng.api.engine.util.TdRuleOperatorMapUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @Author: liuq
 * @Date: 2020/2/14 11:20 AM
 */
public class CustomRuleBuilder extends AbstractRuleBuilder {

    @Override
    protected void convertRuleCondtionElement2RawRule(List<RuleConditionElementDTO> ruleConditionElements, RawRule rawRule){

        if (null == ruleConditionElements || ruleConditionElements.isEmpty() || null == rawRule) {
            return;
        }
        //自定义规则第一层只有1个RuleConditionElementDTO
        if (ruleConditionElements.size() != 1) {
            throw new ParseException("customRule formate error,first layer don't have one elemvent!ruleId:"+rawRule.getId());
        }

        List<Condition> conditionList = Lists.newArrayList();
        List<FunctionDesc> functionDescList = Lists.newArrayList();

        /**
         * 用于给函数计数，把condition和函数对应起来
         */
        Integer num = new Integer(1);
        String logic = processOneLayerAndChildrenElements(ruleConditionElements.get(0), conditionList, functionDescList, num);

        rawRule.setConditionList(conditionList);
        rawRule.setFunctionDescList(functionDescList);
        rawRule.setLogic(logic);
    }

    /**
     * 处理一层，和该层的所有子节点
     *
     * @param elementDTO
     * @param conditionList
     * @param functionDescList
     * @param num
     * @return 返回逻辑表达式:1&(2|4)等
     */
    private String processOneLayerAndChildrenElements(RuleConditionElementDTO elementDTO, List<Condition> conditionList, List<FunctionDesc> functionDescList, Integer num) {
        StringBuilder sb = new StringBuilder();
        /**
         * 只有一个条件的情况
         */
        if (elementDTO.getSubConditions() == null || elementDTO.getSubConditions().isEmpty()) {
            processOneElement(elementDTO, conditionList, functionDescList, num);
            return elementDTO.getId().toString();
        }
        if (StringUtils.isBlank(elementDTO.getLogicOperator())) {
            throw new ParseException("CustomRuleBuilder parse format error,logicOperator blank,conditionUuid:" + elementDTO.getUuid());
        }
        String logic = TdRuleOperatorMapUtils.getEngineTypeFromDisplay(elementDTO.getLogicOperator());
        if (StringUtils.isBlank(logic)) {
            throw new ParseException("CustomRuleBuilder logic error,conditionUuid:" + elementDTO.getUuid() + ",logic:" + elementDTO.getLogicOperator());
        }

        List<RuleConditionElementDTO> subConditions = elementDTO.getSubConditions();
        for (RuleConditionElementDTO ruleConditionElementDTO : subConditions) {
            if (StringUtils.isNotBlank(ruleConditionElementDTO.getLogicOperator())) {
                sb.append(logic);
                sb.append("(");
                sb.append(processOneLayerAndChildrenElements(ruleConditionElementDTO, conditionList, functionDescList, num));
                sb.append(")");
            } else {
                sb.append(logic).append(ruleConditionElementDTO.getId());
                processOneElement(ruleConditionElementDTO, conditionList, functionDescList, num);
            }
        }
        return sb.substring(1);
    }


}
