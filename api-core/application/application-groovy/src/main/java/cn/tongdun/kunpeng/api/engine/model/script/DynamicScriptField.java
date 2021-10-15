package cn.tongdun.kunpeng.api.engine.model.script;

import cn.tongdun.ddd.common.domain.ConcurrencySafeEntity;
import lombok.Data;

@Data
public class DynamicScriptField extends ConcurrencySafeEntity {

    /**
     * 合作方编码 partner_code
     */
    private String partnerCode;

    /**
     * 应用编码 app_name
     */
    private String appName;

    /**
     * 事件类型 event_type
     */
    private String eventType;

    /**
     * 赋值字段来源 field_type
     */
    private String fieldType;

    /**
     * 赋值字段编码 field_code
     */
    private String fieldCode;

    /**
     * 是否已删除 is_deleted
     */
    private boolean deleted;

    /**
     * 脚本字段表uuid
     */
    private String dynamicScriptUuid;

    /**
     * 合作方 partner_name
     */
    private String partnerNameStr;

    /**
     * 应用 app_name
     */
    private String appNameStr;

    /**
     * 事件类型 event_name
     */
    private String eventNameStr;

    /**
     * 字段名称 field_code
     */
    private String fieldNameStr;


}