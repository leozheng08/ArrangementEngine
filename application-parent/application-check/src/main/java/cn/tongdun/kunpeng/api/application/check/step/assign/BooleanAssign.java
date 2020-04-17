package cn.tongdun.kunpeng.api.application.check.step.assign;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: liuq
 * @Date: 2020/4/8 6:50 PM
 */
public class BooleanAssign extends AbstractAssign{
    @Override
    public Object getFieldValue(Object requestValue) {
        Object fieldValue=null;
        if((requestValue instanceof Boolean)) {
            fieldValue = requestValue;
        }else if (StringUtils.isNotBlank(requestValue.toString())){
            fieldValue = "true".equalsIgnoreCase(requestValue.toString()) ? true : false;
        }
        return fieldValue;
    }
}
