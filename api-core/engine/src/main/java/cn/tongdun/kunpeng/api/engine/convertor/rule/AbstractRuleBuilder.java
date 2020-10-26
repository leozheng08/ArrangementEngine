package cn.tongdun.kunpeng.api.engine.convertor.rule;

import cn.fraudmetrix.module.tdrule.action.ActionDesc;
import cn.fraudmetrix.module.tdrule.constant.FieldConstants;
import cn.fraudmetrix.module.tdrule.constant.FieldTypeEnum;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.model.ConditionParam;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.rule.Condition;
import cn.fraudmetrix.module.tdrule.util.FunctionLoader;
import cn.fraudmetrix.module.tdrule.util.RuleCreateFactory;
import cn.tongdun.kunpeng.api.common.util.KunpengStringUtils;
import cn.tongdun.kunpeng.api.engine.constant.IterateType;
import cn.tongdun.kunpeng.api.engine.constant.RuleConstant;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.rule.function.WeightFunction;
import cn.tongdun.kunpeng.api.engine.util.TdRuleOperatorMapUtils;
import cn.tongdun.kunpeng.client.data.PolicyMode;
import cn.tongdun.kunpeng.client.dto.RuleActionElementDTO;
import cn.tongdun.kunpeng.client.dto.RuleConditionElementDTO;
import cn.tongdun.kunpeng.client.dto.RuleDTO;
import cn.tongdun.kunpeng.client.dto.WeightedRiskConfigDTO;
import cn.tongdun.kunpeng.share.json.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: liuq
 * @Date: 2020/2/14 11:22 AM
 */
public abstract class AbstractRuleBuilder implements RuleBuilder {

    /**
     * 序号生成，用于匹配
     */
    protected int num = 0;

    @Override
    public Rule build(RuleDTO ruleDTO) {

        //1.把ruleDTO转换为RawRule
        RawRule rawRule = new RawRule();
        //1.1把RuleDTO的基本信心填充到RawRule中
        rawRule.setId(String.valueOf(ruleDTO.getId()));
        rawRule.setName(ruleDTO.getName());
        rawRule.setType(ruleDTO.getTemplate().trim());
        //1.2把RuleDTO中的条件转为到RawRule
        convertRuleCondtionElement2RawRule(ruleDTO.getRuleConditionElements(), rawRule);
        //1.3把RuleDTO中的动作转为RawRule中的动作
        List<ActionDesc> actionDescList = convertRuleActionElement2RawRule(ruleDTO.getRuleActionElements());
        rawRule.setActionDescList(actionDescList);
        //2.把RawRule转换为Rule
        //2.1通过tdrule的工厂方法把RawRule转为Rule下面的可执行体
        cn.fraudmetrix.module.tdrule.rule.Rule evalRule = RuleCreateFactory.createRule(rawRule);
        if (null == evalRule) {
            throw new ParseException("AbstractRuleBuilder build error,get Null evalRule,ruleUuid:" + ruleDTO.getUuid());
        }
        //2.2构造cn.tongdun.kunpeng.api.engine.model.rule.Rule的基本信息
        Rule rule = buildRuleWithBasicInfo(ruleDTO);
        rule.setEval(evalRule);
        //2.3把权重转为Function
        if (ruleDTO.getWeightedRiskConfigDTO() != null && StringUtils.equalsIgnoreCase(ruleDTO.getMode(), PolicyMode.Weighted.name())) {
            WeightFunction weightFunction = getWeightFunctionFromDTO(ruleDTO);
            rule.setWeightFunction(weightFunction);
        }
        return rule;
    }

    private WeightFunction getWeightFunctionFromDTO(RuleDTO ruleDTO) {
        if (null == ruleDTO || ruleDTO.getWeightedRiskConfigDTO() == null) {
            return null;
        }
        WeightedRiskConfigDTO riskConfigDTO = ruleDTO.getWeightedRiskConfigDTO();
        FunctionDesc functionDesc = new FunctionDesc();
        functionDesc.setId(ruleDTO.getId().intValue());
        functionDesc.setRuleUuid(ruleDTO.getUuid());
        functionDesc.setType("ruleWeight");

        List<FunctionParam> paramList = Lists.newArrayList();

        FunctionParam baseWeight = new FunctionParam();
        baseWeight.setType(FieldTypeEnum.INPUT.name());
        baseWeight.setValue(Double.valueOf(riskConfigDTO.getBaseWeight()).toString());
        baseWeight.setName("baseWeight");
        paramList.add(baseWeight);

        FunctionParam weightRatio = new FunctionParam();
        weightRatio.setType(FieldTypeEnum.INPUT.name());
        weightRatio.setValue(Double.valueOf(riskConfigDTO.getWeightRatio()).toString());
        weightRatio.setName("weightRatio");
        paramList.add(weightRatio);

        FunctionParam weightIndex = new FunctionParam();
        weightIndex.setType(riskConfigDTO.getWeightProperty());
        weightIndex.setValue(riskConfigDTO.getWeightPropertyValue());
        weightIndex.setName("weightProperty");
        paramList.add(weightIndex);

        if (riskConfigDTO.getLowerLimitScore() != null) {
            FunctionParam lowerLimitScore = new FunctionParam();
            lowerLimitScore.setType(FieldTypeEnum.INPUT.name());
            lowerLimitScore.setValue(riskConfigDTO.getLowerLimitScore().toString());
            lowerLimitScore.setName("lowerLimitScore");
            paramList.add(lowerLimitScore);
        }

        if (riskConfigDTO.getUpperLimitScore() != null) {
            FunctionParam upperLimitScore = new FunctionParam();
            upperLimitScore.setType(FieldTypeEnum.INPUT.name());
            upperLimitScore.setValue(riskConfigDTO.getUpperLimitScore().toString());
            upperLimitScore.setName("upperLimitScore");
            paramList.add(upperLimitScore);
        }

        functionDesc.setParamList(paramList);
        return (WeightFunction) FunctionLoader.getFunction(functionDesc);
    }

    private Rule buildRuleWithBasicInfo(RuleDTO ruleDTO) {
        Rule rule = new Rule();
        rule.setRuleId(ruleDTO.getId().toString());
        rule.setRuleCustomId(ruleDTO.getRuleCustomId());
        rule.setUuid(ruleDTO.getUuid());
        rule.setDisplayOrder(ruleDTO.getPriority());
        rule.setName(ruleDTO.getName());
        rule.setParentUuid(ruleDTO.getParentUuid());
        rule.setBizType(ruleDTO.getBizType());
        rule.setBizUuid(ruleDTO.getBizUuid());
        rule.setTemplate(ruleDTO.getTemplate());
        rule.setMode(ruleDTO.getMode());
        rule.setDecision(ruleDTO.getRiskDecision());
        rule.setBizType(ruleDTO.getBizType());
        rule.setBizUuid(ruleDTO.getBizUuid());
        rule.setGmtModify(ruleDTO.getGmtModify());
        return rule;
    }

    /**
     * 把规则中的动作转为RawRule中的动作
     *
     * @param ruleActionElements
     */
    private List<ActionDesc> convertRuleActionElement2RawRule(List<RuleActionElementDTO> ruleActionElements) {
        if (null == ruleActionElements || ruleActionElements.isEmpty()) {
            return Collections.emptyList();
        }
        List<ActionDesc> actionDescList = new ArrayList<>();
        for (RuleActionElementDTO ruleActionElementDTO : ruleActionElements) {
            String params = ruleActionElementDTO.getActions();
            if (StringUtils.isBlank(params)) {
                continue;
            }
            List array = JSON.parseArray(params, HashMap.class);
            if (array.isEmpty()) {
                continue;
            }
            long i = 0;
            for (Object obj : array) {
                ActionDesc actionDesc = new ActionDesc();
                actionDesc.setId(++i);
                actionDesc.setType(ruleActionElementDTO.getActionType());
                actionDesc.setParams(JSON.toJSONString(obj));
                actionDescList.add(actionDesc);
            }
        }
        return actionDescList;
    }

    /**
     * 构造左变量
     *
     * @param elementDTO
     * @param functionDescList
     * @return
     */
    private ConditionParam constructLeft(RuleConditionElementDTO elementDTO, List<FunctionDesc> functionDescList) {
        ConditionParam left = new ConditionParam();
        if (StringUtils.equalsIgnoreCase("alias", elementDTO.getLeftPropertyType())) {
            //左变量是函数
            ++num;
            FunctionDesc functionDesc = new FunctionDesc();
            functionDesc.setId(num);
            functionDesc.setType(elementDTO.getLeftProperty());
            functionDesc.setConditionUuid(elementDTO.getUuid());
            functionDesc.setDescription(elementDTO.getDescription());
            functionDesc.setRuleUuid(elementDTO.getBizUuid());
            List<FunctionParam> paramList = JSON.parseArray(elementDTO.getParams(), FunctionParam.class);
            functionDesc.setParamList(paramList);
            functionDescList.add(functionDesc);
            functionDesc.putExtProperty(RuleConstant.FUNC_DESC_PARAMS_ALL,"true");

            left.setValue(String.valueOf(num));
            left.setFieldType(FieldTypeEnum.FUNC);

        } else {
            FieldTypeEnum fieldTypeEnum = FieldTypeEnum.getFieldType(elementDTO.getLeftPropertyType());
            if (null == fieldTypeEnum) {
                throw new ParseException("AbstractRuleBuilder processOneElement error,FieldType not exist!LeftPropertyType:"
                        + elementDTO.getLeftPropertyType() + "conditionUuid:" + elementDTO.getUuid());
            }
            left.setFieldType(fieldTypeEnum);
            left.setDataType(elementDTO.getLeftPropertyDataType());
            left.setName(elementDTO.getLeftProperty());
        }
        left.setConditionId(elementDTO.getId().intValue());

        //平台指标需要考虑原始值
        if (elementDTO.isLeftUseOriginValue()) {
            left.putExtProperty(FieldConstants.INDEX_USE_ORIGIN_VALUE, true);
        }
        return left;
    }

    /**
     * 构造右变量
     *
     * @param elementDTO
     * @return
     */
    private ConditionParam constructRight(RuleConditionElementDTO elementDTO) {
        ConditionParam right = new ConditionParam();
        FieldTypeEnum fieldTypeEnum = FieldTypeEnum.getFieldType(elementDTO.getRightType());
        if (null == fieldTypeEnum) {
            return null;
//            throw new ParseException("AbstractRuleBuilder processOneElement error,FieldType not exist!RightType:"
//                    + elementDTO.getRightType() + "conditionUuid:" + elementDTO.getUuid());
        }
        right.setFieldType(fieldTypeEnum);
        right.setDataType(elementDTO.getRightDataType());
        if (fieldTypeEnum == FieldTypeEnum.INPUT) {
            right.setValue(elementDTO.getRightValue());
        }
        right.setName(elementDTO.getRightValue());
        right.setConditionId(elementDTO.getId().intValue());

        //平台指标需要考虑原始值
        if (elementDTO.isRightUseOriginValue()) {
            right.putExtProperty(FieldConstants.INDEX_USE_ORIGIN_VALUE, true);
        }

        return right;
    }

    /**
     * 构造操作符
     *
     * @param elementDTO
     * @return
     */
    private String constructOperator(RuleConditionElementDTO elementDTO) {
        String op = TdRuleOperatorMapUtils.getEngineTypeFromDisplay(elementDTO.getOp());
        if (StringUtils.isBlank(op)) {
            throw new ParseException("AbstractRuleBuilder processOneElement error,can't map operator:" + elementDTO.getOp());
        }

        //equals与notEquals支持数组迭代方式
        if(!"equals".equals(op) && !"notEquals".equals(op)){
            return op;
        }

        if(StringUtils.isBlank(elementDTO.getParams())) {
            return op;
        }

        List<FunctionParam> paramList = JSON.parseArray(elementDTO.getParams(), FunctionParam.class);
        if(paramList == null || paramList.isEmpty()) {
            return op;
        }
        //当iterateType=any ，op=equals 操作符转为 arrayAnyEquals
        //当iterateType=any ，op=notEquals 操作符转为 arrayAnyNotEquals
        //当iterateType=all ，op=equals 操作符转为 arrayAllEquals
        //当iterateType=all ，op=notEquals 操作符转为 arrayAllNotEquals
        for(FunctionParam param : paramList) {
            if(!"iterateType".equalsIgnoreCase(param.getName())){
                continue;
            }
            if(IterateType.ALL.name().equalsIgnoreCase(param.getValue())){
                op = "arrayAll" + KunpengStringUtils.upperCaseFirstChar(op);
            } else if(IterateType.ANY.name().equalsIgnoreCase(param.getValue())){
                op = "arrayAny" + KunpengStringUtils.upperCaseFirstChar(op);
            }
//            break;
        }
        return op;
    }

    /**
     * 处理一个condition,可能是左右变量的形式，也可能是函数形式的条件
     *
     * @param elementDTO
     * @param conditionList
     * @param functionDescList
     */
    protected void processOneElement(RuleConditionElementDTO elementDTO, List<Condition> conditionList, List<FunctionDesc> functionDescList) {
        Condition condition = new Condition();
        condition.setId(elementDTO.getId().intValue());
        condition.setConditionUuid(elementDTO.getUuid());
        condition.setDescription(elementDTO.getDescription());
        condition.setRuleUuid(elementDTO.getBizUuid());
        //1.构造左变量
        ConditionParam left = constructLeft(elementDTO, functionDescList);
        condition.setLeft(left);
        //2.构造右变量
        ConditionParam right = constructRight(elementDTO);
        condition.setRight(right);
        //3.构造操作符
        String op = constructOperator(elementDTO);

        condition.setOp(op);
        conditionList.add(condition);
    }

    /**
     * 把规则中的条件转换为RawRule中的条件和函数
     *
     * @param ruleConditionElements
     * @param rawRule
     */
    protected abstract void convertRuleCondtionElement2RawRule(List<RuleConditionElementDTO> ruleConditionElements, RawRule rawRule);
}
