package cn.tongdun.kunpeng.api.engine.model.field;

import cn.tongdun.kunpeng.api.engine.model.StatusEntity;
import cn.tongdun.kunpeng.api.common.data.IFieldDefinition;
import lombok.Data;

/**
 *
 */
@Data
public class FieldDefinition extends StatusEntity implements IFieldDefinition {
    private static final long serialVersionUID = 8963907847949013871L;

    /**
     * sys为系统字段 ext为扩展字段 field_type
     */
    private String fieldType;

    /**
     * 合作方 partner_code
     */
    private String partnerCode;

    /**
     * 应用名 app_name
     */
    private String appName;

    /**
     * 应用类型 app_type
     */
    private String appType;

    /**
     * 字段code field_code
     */
    private String fieldCode;

    /**
     * field_name
     */
    private String fieldName;

    /**
     * 字段显示名称 display_name
     */
    private String displayName;

    /**
     * 字段属性列，用于追加字段所属类型idNumber:身份证 mobile:手机号 email:邮箱
     */
    private String property;

    /**
     * 事件类型 event_type
     */
    private String eventType;

    /**
     * 字段数据类型 string、int、double等 data_type
     */
    private String dataType;

    /**
     * 最大长度 max_length
     */
    private Integer maxLength;

    /**
     * 描述 description
     */
    private String description;

    /**
     * 是否velocity字段 is_velocity_field
     */
    private boolean velocityField;

    /**
     * 字段的使用场景 usage_scene
     */
    private String usageScene;


    @Override
    public boolean isExtField() {
        return "ext".equals(fieldType);
    }

}
