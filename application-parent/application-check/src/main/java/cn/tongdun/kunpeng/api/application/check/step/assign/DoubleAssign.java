package cn.tongdun.kunpeng.api.application.check.step.assign;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: liuq
 * @Date: 2020/4/8 6:45 PM
 */
public class DoubleAssign extends AbstractAssign{
    @Override
    public Object getFieldValue(Object requestValue) {
        Object fieldValue=null;
        if(requestValue instanceof Number){
            fieldValue = ((Number)requestValue).doubleValue();
        }else if (StringUtils.isNotBlank(requestValue.toString())){
            fieldValue = Double.valueOf(requestValue.toString());
        }
        return fieldValue;
    }
}
