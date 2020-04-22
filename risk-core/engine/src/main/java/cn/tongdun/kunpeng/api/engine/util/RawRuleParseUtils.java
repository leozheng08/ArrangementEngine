package cn.tongdun.kunpeng.api.engine.util;

import cn.fraudmetrix.module.tdrule.constant.FieldTypeEnum;
import cn.fraudmetrix.module.tdrule.eval.Variable;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.operator.AbstractBinaryOperator;
import cn.fraudmetrix.module.tdrule.rule.Condition;
import cn.fraudmetrix.module.tdrule.util.FunctionLoader;
import cn.fraudmetrix.module.tdrule.util.OperatorLoader;

/**
 * @Author: liuq
 * @Date: 2020/2/13 7:02 PM
 */
public class RawRuleParseUtils {

    /**
     * 解析左变量是函数，右变量有值的规则定义
     *
     * @param rawRule
     * @return
     */
    public static AbstractBinaryOperator parseWithFunctionAndRight(RawRule rawRule) {

        if (null == rawRule || rawRule.getFunctionDescList() == null || rawRule.getFunctionDescList().isEmpty()) {
            throw new ParseException("RawRuleParseUtils parse error!null == rawRule or rawRule.getFunctionDescList is blank!");
        }
        if (rawRule.getFunctionDescList().size() > 1) {
            throw new ParseException("RawRuleParseUtils parse error!expect 1 FunctionDesc,but input :" + rawRule.getFunctionDescList().size());
        }
        FunctionDesc functionDesc = rawRule.getFunctionDescList().get(0);
        AbstractFunction abstractFunction = (AbstractFunction) FunctionLoader.getFunction(functionDesc);
        //构造比较条件
        if (rawRule.getConditionList() == null || rawRule.getConditionList().isEmpty() || rawRule.getConditionList().size() != 1) {
            throw new ParseException("RawRuleParseUtils parse condition error,ruleUuid:" + abstractFunction.getRuleUuid() + ",conditionUuid:" + abstractFunction.getConditionUuid());
        }

        Condition condition = rawRule.getConditionList().get(0);
        if (condition.getLeft().getFieldType() != FieldTypeEnum.FUNC ||
                !condition.getLeft().getValue().equals(functionDesc.getId().toString())) {
            throw new ParseException("RawRuleParseUtils condition not match function error,ruleUuid:" + abstractFunction.getRuleUuid() + ",conditionUuid:" + abstractFunction.getConditionUuid());
        }

        Variable right = ConditionParamUtils.parseConditionParam(condition.getRight());
        if (null == right) {
            throw new ParseException("RawRuleParseUtils condition parse error,conditionUuid:" + abstractFunction.getConditionUuid());
        }
        AbstractBinaryOperator operator = (AbstractBinaryOperator) OperatorLoader.getOperator(condition.getOp());
        if (null == operator) {
            throw new ParseException("RawRuleParseUtils getOperator error,conditionUuid:" + abstractFunction.getConditionUuid() + ",op:" + condition.getOp());
        }
        operator.addOperand(abstractFunction);
        operator.addOperand(right);
        return operator;
    }
}
