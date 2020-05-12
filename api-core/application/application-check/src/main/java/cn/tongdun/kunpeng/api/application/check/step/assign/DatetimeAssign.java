package cn.tongdun.kunpeng.api.application.check.step.assign;

import cn.tongdun.kunpeng.api.common.util.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * @Author: liuq
 * @Date: 2020/4/8 6:47 PM
 */
public class DatetimeAssign extends AbstractAssign{
    @Override
    public Object getFieldValue(Object requestValue) {
        Object fieldValue=null;
        if((requestValue instanceof Date)) {
            fieldValue = requestValue;
        }else if (StringUtils.isNotBlank(requestValue.toString())){
            try {
                fieldValue = DateUtil.parseDateTime(requestValue.toString());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return fieldValue;
    }
}
