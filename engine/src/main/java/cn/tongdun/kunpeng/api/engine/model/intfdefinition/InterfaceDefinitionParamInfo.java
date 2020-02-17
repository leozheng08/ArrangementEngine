package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import lombok.Data;

@Data
public class InterfaceDefinitionParamInfo {
    /**
     * 规则引擎字段名
     */
    private String  ruleField;

    /**
     * dubbo方法参数名
     */
    private String  interfaceField;

    /**
     * dubbo方法参数类型
     */
    private String  interfaceType;

    /**
     * 规则引擎字段值
     */
    private Object  value;

    /**
     * 是否必填字段
     */
    private boolean isNecessary;

    /**
     * 是否为数组
     */
    private boolean isArray;

    /**
     * 参数类型：普通，指标拼接，字段拼接
     */
    private String  type;

}
