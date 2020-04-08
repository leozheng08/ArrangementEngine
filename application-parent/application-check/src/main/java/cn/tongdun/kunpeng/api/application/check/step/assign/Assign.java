package cn.tongdun.kunpeng.api.application.check.step.assign;

import cn.tongdun.kunpeng.common.data.IFieldDefinition;

import java.util.Map;

/**
 * @Author: liuq
 * @Date: 2020/4/8 6:14 PM
 */
public interface Assign {

    void execute(Map<String, Object> fields, IFieldDefinition fieldDefinition, Object requestValue);
}
