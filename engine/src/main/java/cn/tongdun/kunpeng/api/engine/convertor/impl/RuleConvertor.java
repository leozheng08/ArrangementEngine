package cn.tongdun.kunpeng.api.engine.convertor.impl;

import cn.fraudmetrix.module.tdrule.action.ActionDesc;
import cn.fraudmetrix.module.tdrule.constant.FieldTypeEnum;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.model.ConditionParam;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.rule.Condition;
import cn.fraudmetrix.module.tdrule.util.RuleCreateFactory;
import cn.tongdun.ddd.common.exception.BasicErrorCode;
import cn.tongdun.ddd.common.exception.BizException;
import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.dto.RuleActionElementDTO;
import cn.tongdun.kunpeng.api.engine.dto.RuleConditionElementDTO;
import cn.tongdun.kunpeng.api.engine.dto.RuleDTO;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 上午10:15
 */
@Component
@DependsOn(value = "defaultConvertorFactory")
public class RuleConvertor implements IConvertor<RuleDTO,Rule> {

    private Logger logger = LoggerFactory.getLogger(PipelineExecutor.class);

    private Map<String,String> operatorMap = new HashMap<>();

    private Map<String,String> funcationMap = new HashMap<>();

    @Autowired
    DefaultConvertorFactory convertorFactory;

    @PostConstruct
    public void init(){
        operatorMap.put("<","lt");
        operatorMap.put("<=","lee");
        operatorMap.put(">=","ge");
        operatorMap.put(">","gte");
        operatorMap.put("==","equals");
        operatorMap.put("!=","notEquals");
        operatorMap.put("||","or");
        operatorMap.put("&&","and");

        operatorMap.put("isinteger","isInteger");
        operatorMap.put("isdivisible","isDivisible");
        operatorMap.put("include","containString"); //待确认
        operatorMap.put("exclude","notContainString");//待确认
        operatorMap.put("prefix","待实现");
        operatorMap.put("suffix","待实现");

        convertorFactory.register(RuleDTO.class,this);
    }


    @Override
    public Rule convert(RuleDTO dto){
        Rule result = new Rule();
        //规则基本信息
        result.setRuleId(dto.getId().toString());
        result.setRuleCustomId(dto.getRuleCustomId());
        result.setUuid(dto.getUuid());
        result.setDisplayOrder(dto.getPriority());
        result.setName(dto.getName());
        result.setParentUuid(dto.getParentUuid());
        result.setSubPolicyUuid(dto.getPolicyUuid());
        result.setTemplate(dto.getTemplate());
        result.setDecision(dto.getRiskDecision());

        //生成规则引擎的RawRule对象
        RawRule rawRule = new RawRule();
        rawRule.setId(dto.getId().toString());
        rawRule.setName(dto.getName());
        rawRule.setType(dto.getTemplate());

        //生成条件
        List<RuleConditionElementDTO> conditionElements = dto.getRuleConditionElements();
        if(conditionElements == null || conditionElements.isEmpty()){
            throw new BizException(BasicErrorCode.SYS_ERROR,"RuleConvertor error RuleConditionElement is empty");
        }
        List<Condition> conditionList = new ArrayList<>();
        List<FunctionDesc> functionDescList = new ArrayList<>();

        //生成条件的逻辑操作表达式
        StringBuilder buffer = new StringBuilder();
        // 如1&2&3  1|2|3
        String logic = null;
        int i=0;
        for(RuleConditionElementDTO conditionElementDO:conditionElements){
            if(i==0){
                logic =  convertlogic(conditionElementDO.getLogicOperator());
            } else {
                if(logic == null){
                    throw new BizException(BasicErrorCode.SYS_ERROR,"RuleConvertor error LogicOperator is null");
                }
                buffer.append(logic);
            }
            buffer.append(conditionElementDO.getId());

            Condition condition = convertCondition(conditionElementDO,functionDescList);
            conditionList.add(condition);

            i++;
        }
        rawRule.setLogic(buffer.toString());
        rawRule.setFunctionDescList(functionDescList);
        rawRule.setConditionList(conditionList);

        //生成action
        List<ActionDesc> actionDescList = new ArrayList<>();
        List<RuleActionElementDTO> ruleActionElementDOList= dto.getRuleActionElements();
        if(ruleActionElementDOList != null){
            for(RuleActionElementDTO ruleActionElementDO:ruleActionElementDOList){
                convertAction(actionDescList,ruleActionElementDO.getActions());
            }
        }
        rawRule.setActionDescList(actionDescList);

        //创建规则引擎的可计算的Rule对象
        cn.fraudmetrix.module.tdrule.rule.Rule rule = RuleCreateFactory.createRule(rawRule);
        result.setEval(rule);

        //权重函数

        return result;
    }

    /**
     private double                       baseWeight;                              // 风险权重基线
     private double                       weightRatio;                             // 风险权重比例
     private String                       weightIndex;                             // 风险权重指标　
     private String                       indexType;                               // 指标类型    旧的为空,新的GAEA_INDICATRIX　
     private Double                       downLimitScore;                          // 权重计算分数右值下限
     private Double                       upLimitScore;                            // 权重计算分数右值上限
     */


    private void convertAction(List<ActionDesc> actionDescList ,String params){
        if(params == null){
            return;
        }


        JSONArray array = JSONArray.parseArray(params);
        if(array.isEmpty()){
            return;
        }

        long i = 0;
        for(Object obj :array){
            JSONObject actionJson = (JSONObject)obj;
            ActionDesc actionDesc = new ActionDesc();
            actionDesc.setId(++i);
            actionDesc.setType("assignment");
            actionDesc.setParams(obj.toString());
            actionDescList.add(actionDesc);
        }

        return;
    }




    /**
     * 逻辑操作符转换
     * @param source
     * @return
     */
    private String convertlogic(String source){
        if(source == null){
            return null;
        }
        return source.replaceAll("&&","&");

    }

    /**
     * 操作符转换
     * @param source
     * @return
     */
    private String convertOperator(String source){
        if(source == null){
            return null;
        }
        String target = operatorMap.get(source);
        if(target == null){
            target = source;
        }
        return target;
    }


    public void testFunction() {







    }


    public Condition convertCondition(RuleConditionElementDTO t, List<FunctionDesc> functionDescList ){


        Condition condition = new Condition();

        condition.setId(t.getId().intValue());
        condition.setOp(convertOperator(t.getOp()));

        ConditionParam leftParam = new ConditionParam();
        leftParam.setConditionId(condition.getId());
        leftParam.setDataType(convertDataType(t.getLeftPropertyDataType()));
        leftParam.setName(t.getLeftProperty());

        FieldTypeEnum fieldType = convertFieldType(t.getLeftPropertyType(),t.getLeftPropertyDataType());
        leftParam.setFieldType(fieldType);
        if(fieldType != null) {
            switch (fieldType) {
                case FUNC:
                    FunctionDesc functionDesc = convertFun(t.getId().intValue(), t.getLeftProperty(), t.getParams());
                    functionDescList.add(functionDesc);
                    break;
                case PLATFORM_INDEX:
                    break;
            }
        }

        return condition;
    }

    private FunctionDesc convertFun(Integer id,String funName,String params){
        FunctionDesc functionDesc = new FunctionDesc();
        functionDesc.setId(id);
        functionDesc.setType(funName);

        JSONArray jsonArray = JSONArray.parseArray(params);

        List<FunctionParam> paramList=new ArrayList<>();
        functionDesc.setParamList(paramList);
        for(Object obj:jsonArray){
            JSONObject paramsJson = (JSONObject)obj;

            FunctionParam functionParam=new FunctionParam();
            functionParam.setName(paramsJson.getString("name"));
            functionParam.setType(paramsJson.getString("type"));
            functionParam.setValue(paramsJson.getString("value"));
            paramList.add(functionParam);
        }

        return functionDesc;
    }


    /**
     * 转换数据类型
     BOOLEAN->boolean
     INT->int
     DOUBLE->double
     STRING->String

     DATETIME->
     GAEA_INDICATRIX->
     INDEX_INT->
     ARRAY->
     * @param source
     * @return
     */
    private String convertDataType(String source){
        if(source == null){
            return null;
        }
        String target = null;

        target = source.toLowerCase();

        return target;
    }



    private FieldTypeEnum convertFieldType(String type,String dataType){
        if(type == null){
            return null;
        }

        FieldTypeEnum fieldType = null;
        switch (type){
            case "input":
                fieldType = FieldTypeEnum.INPUT;
                break;
            case "alias":
                fieldType = FieldTypeEnum.FUNC;
                break;
            case "context":
                if(dataType != null && dataType.equals("GAEA_INDICATRIX")){
                    fieldType = FieldTypeEnum.PLATFORM_INDEX;
                } else{
                    fieldType = FieldTypeEnum.CONTEXT;
                }
                break;
        }

        return fieldType;
    }

}
