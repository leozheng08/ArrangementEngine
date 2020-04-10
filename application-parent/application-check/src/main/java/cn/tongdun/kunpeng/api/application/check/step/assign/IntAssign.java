package cn.tongdun.kunpeng.api.application.check.step.assign;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: liuq
 * @Date: 2020/4/8 6:43 PM
 */
public class IntAssign extends AbstractAssign{
    @Override
    public Object getFieldValue(Object requestValue) {
        Object fieldValue=null;
        if(requestValue instanceof Number){
            fieldValue = ((Number)requestValue).intValue();
        } else if (StringUtils.isNotBlank(requestValue.toString())){
            fieldValue = Integer.valueOf(requestValue.toString());
        }
        return fieldValue;
    }
}
