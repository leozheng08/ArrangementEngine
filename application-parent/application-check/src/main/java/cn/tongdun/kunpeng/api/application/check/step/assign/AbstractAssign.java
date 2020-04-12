package cn.tongdun.kunpeng.api.application.check.step.assign;

import cn.tongdun.kunpeng.api.common.data.IFieldDefinition;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @Author: liuq
 * @Date: 2020/4/8 6:36 PM
 */
public abstract class AbstractAssign implements Assign{

    @Override
    public void execute(Map<String, Object> fields, IFieldDefinition fieldDefinition, Object requestValue){

        Object fieldValue=getFieldValue(requestValue);

        //根据所属类型（idNumber:身份证 mobile:手机号 email:邮箱）对值做处理
        fieldValue = getValueByPropertyType(fieldDefinition,fieldValue);

        if(fieldValue == null) {
            return;
        }
        fields.put(fieldDefinition.getFieldCode(), fieldValue);
    }
    /**
     * 根据所属类型（idNumber:身份证 mobile:手机号 email:邮箱）对值做处理
     * @param fieldDefinition
     * @param fieldValue
     * @return
     */
    private Object getValueByPropertyType(IFieldDefinition fieldDefinition,Object fieldValue){
        if(fieldValue == null){
            return fieldValue;
        }

        //所属类型idNumber:身份证 mobile:手机号 email:邮箱
        String property = fieldDefinition.getProperty();
        if(property == null){
            return fieldValue;
        }

        //身份证做小写处理
        if("idNumber".equals(property)){
            if (fieldValue instanceof List) {
                List<?> list = (List<?>) fieldValue;
                CollectionUtils.transform(list, input -> input.toString().toUpperCase());
            } else {
                fieldValue = fieldValue.toString().toUpperCase();
            }
        }

        return fieldValue;
    }
    public abstract Object getFieldValue(Object requestValue);
}
