package cn.tongdun.kunpeng.api.engine.dto;

import lombok.Data;

import java.util.List;

/**
 * 规则条件
 *
 */
@Data
public class RuleConditionElementDTO extends CommonDTO {

    private static final long            serialVersionUID   = 7768242359914265183L;

    /**
     * 父uuid 自定义规则时使用到 parent_uuid
     */
    private String parentUuid;

    /**
     * 逻辑操作符 and：条件都满足 or：至少一个条件满足 !:条件都不满足 !or:至少一条不满足  logic_operator
     */
    private String logicOperator;

    /**
     * 属性，如字段名、指标id等 left_property
     */
    private String leftProperty;

    /**
     * 左变量类型，可以是input，也可以是context  left_property_type
     */
    private String leftPropertyType;

    /**
     * 左变量数据类型 DOUBLE、STRING left_property_data_type
     */
    private String leftPropertyDataType;

    /**
     * 操作符 =,>,<等 op
     */
    private String op;

    /**
     * 右边条件值 right_value
     */
    private String rightValue;

    /**
     * 右边属性类型，context，alias filed、input right_type
     */
    private String rightType;

    /**
     * 右变量类型，可以是GAEA_INDICATRIX、INT、BOOLEAN等 right_data_type
     */
    private String rightDataType;

    /**
     * 描述 description
     */
    private String description;

    /**
     * 左变量是否是使用离线指标原始值，针对左变量类型是平台指标的情况：1 是 0 否 is_left_use_origin_value
     */
    private boolean leftUseOriginValue;

    /**
     * 右变量是否是使用离线指标原始值，针对右变量类型是平台指标的情况：1 是 0 否 is_right_use_origin_value
     */
    private boolean rightUseOriginValue;

    /**
     * 优先级 priority
     */
    private Integer priority;

    /**
     * 扩展字段，kv结构 attribute
     */
    private String attribute;

    /**
     * 业务uuid biz_uuid
     */
    private String bizUuid;

    /**
     * 业务类型 rule:规则 common:通用 output：自定义输出 biz_type
     */
    private String bizType;

    /**
     * 条件数据 json格式 params
     *
     * 数据结构：http://wiki.tongdun.me/pages/viewpage.action?pageId=34044836
     */
    private String params;

    /**
     * 子条件
     */
    private List<RuleConditionElementDTO> subConditions;



}
