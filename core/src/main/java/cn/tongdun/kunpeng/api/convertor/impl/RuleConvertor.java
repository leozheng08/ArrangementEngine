package cn.tongdun.kunpeng.api.convertor.impl;

import cn.fraudmetrix.module.tdrule.constant.FieldTypeEnum;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.model.ConditionParam;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.fraudmetrix.module.tdrule.model.RawRule;
import cn.fraudmetrix.module.tdrule.rule.Condition;
import cn.fraudmetrix.module.tdrule.util.RuleCreateFactory;
import cn.tongdun.ddd.common.exception.BizException;
import cn.tongdun.kunpeng.api.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.convertor.IConvertor;
import cn.tongdun.kunpeng.api.dataobject.RuleConditionElementDO;
import cn.tongdun.kunpeng.api.dataobject.RuleDO;
import cn.tongdun.kunpeng.api.rule.Rule;
import cn.tongdun.kunpeng.api.runtime.IExecutor;
import cn.tongdun.tdframework.core.logger.Logger;
import cn.tongdun.tdframework.core.logger.LoggerFactory;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import com.alibaba.fastjson.JSON;
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
public class RuleConvertor implements IConvertor<RuleDO,Rule> {

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

        convertorFactory.register(RuleDO.class,this);
    }


    @Override
    public Rule convert(RuleDO t){
        Rule result = new Rule();
        result.setRuleId(t.getId().toString());
        result.setRuleCustomId(t.getRuleCustomId());
        result.setUuid(t.getUuid());
        result.setDisplayOrder(t.getDisplayOrder());
        result.setName(t.getName());
        result.setParentUuid(t.getParentUuid());
        result.setSubPolicyUuid(t.getFkPolicyUuid());
        result.setTemplate(t.getTemplate());
        result.setDecision(t.getOperateCode());


        RawRule rawRule = new RawRule();
        rawRule.setId(t.getId().toString());
        rawRule.setName(t.getName());
        rawRule.setType(t.getTemplate());

        List<RuleConditionElementDO> conditionElements = t.getRuleConditionElements();
        if(conditionElements == null || conditionElements.isEmpty()){
            logger.error("RuleConvertor error RuleConditionElement is empty:"+t);
            throw new BizException("RuleConvertor error RuleConditionElement is empty");
        }

        List<Condition> conditionList = new ArrayList<>();
        List<FunctionDesc> functionDescList = new ArrayList<>();

        //生成逻辑操作表达式
        StringBuilder buffer = new StringBuilder();
        String logic = null;
        int i=0;
        for(RuleConditionElementDO conditionElementDO:conditionElements){
            if(i==0){
                logic =  convertlogic(conditionElementDO.getLogicOperator());
            } else {
                if(logic == null){
                    logger.error("RuleConvertor error LogicOperator is null:"+t);
                    throw new BizException("RuleConvertor error LogicOperator is null");
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
        cn.fraudmetrix.module.tdrule.rule.Rule rule = RuleCreateFactory.createRule(rawRule);

        result.setEval(rule);
        return result;
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
        String target = source.replaceAll("&&","&");
        return target;
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


    public Condition convertCondition(RuleConditionElementDO t,List<FunctionDesc> functionDescList ){


        Condition condition = new Condition();

        condition.setId(t.getId().intValue());
        condition.setOp(convertOperator(t.getOperator()));

        ConditionParam leftParam = new ConditionParam();
        leftParam.setConditionId(condition.getId());
        FieldTypeEnum fieldType = convertFieldType(t.getType(),t.getPropertyDataType());
        leftParam.setFieldType(fieldType);
        leftParam.setDataType(convertDataType(t.getPropertyDataType()));
        leftParam.setName(t.getProperty());

        switch (fieldType){
            case FUNC:
                FunctionDesc functionDesc = convertFun(t.getId().intValue(),t.getProperty(), t.getParams());
                functionDescList.add(functionDesc);
                break;
            case INDEX:
                break;
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
                    fieldType = FieldTypeEnum.INDEX;
                } else{
                    fieldType = FieldTypeEnum.CONTEXT;
                }
                break;
        }

        return fieldType;
    }

}
