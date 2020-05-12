package cn.tongdun.kunpeng.api.application.check.step.assign;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: liuq
 * @Date: 2020/4/8 6:51 PM
 */
public class ArrayAssign extends AbstractAssign {
    @Override
    public Object getFieldValue(Object requestValue) {
        Object fieldValue = null;
        if (requestValue instanceof List) {
            fieldValue = requestValue;
        } else if (requestValue instanceof Object[]) {
            fieldValue = Arrays.asList((Object[]) requestValue);
        } else if (StringUtils.isNotBlank(requestValue.toString())) {
            fieldValue = Arrays.asList(requestValue.toString().replaceAll("，", ",").split(","));
        }
        return fieldValue;
    }
}
