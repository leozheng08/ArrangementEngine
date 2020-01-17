package cn.tongdun.kunpeng.api.convertor.impl;

import cn.fraudmetrix.module.tdrule.constant.FieldTypeEnum;
import cn.fraudmetrix.module.tdrule.model.ConditionParam;
import cn.fraudmetrix.module.tdrule.rule.Condition;
import cn.tongdun.kunpeng.api.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.convertor.IConvertor;
import cn.tongdun.kunpeng.api.dto.RuleConditionElementDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 上午10:15
 */
@Component
@DependsOn(value = "defaultConvertorFactory")
public class RuleConditionElementConvertor implements IConvertor<RuleConditionElementDTO,Condition> {


    private Map<String,String> operatorMap = new HashMap<>();

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
        convertorFactory.register(RuleConditionElementDTO.class,this);
    }

    @Override
    public Condition convert(RuleConditionElementDTO t){


        Condition condition = new Condition();

        condition.setId(t.getId().intValue());
        condition.setOp(convertOperator(t.getOp()));

        ConditionParam leftParam = new ConditionParam();
        leftParam.setConditionId(condition.getId());
        FieldTypeEnum fieldType = convertFieldType(t.getLeftPropertyType(),t.getLeftPropertyDataType());
        leftParam.setFieldType(fieldType);
        leftParam.setDataType(convertDataType(t.getLeftPropertyDataType()));
        leftParam.setName(t.getLeftProperty());



        return null;
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
