package cn.tongdun.kunpeng.client.dto;

import lombok.Data;

/**
 * @Author: changkai.yang
 * @Date: 2020/4/20 上午10:16
 */
@Data
public class DynamicScriptDTO extends CommonDTO {
    /**
     * 名称 script_name
     */
    private String scriptName;

    /**
     * 赋值字段 assign_field
     */
    private String assignField;

    /**
     * 描述 description
     */
    private String description;

    /**
     * 事件类型 event_type
     */
    private String eventType;

    /**
     * 脚本字段类型 string、int、double等
     */
    private String dataType;

    /**
     * 代码 script_code
     */
    private String scriptCode;
}
