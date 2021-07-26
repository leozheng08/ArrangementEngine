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
     * 合作方 partner_code
     */
    private String partnerCode;

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
     * 脚本类型 groovy script_type
     */
    private String scriptType;

    /**
     * 代码 script_code
     */
    private String scriptCode;
}
