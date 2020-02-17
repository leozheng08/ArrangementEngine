package cn.tongdun.kunpeng.api.engine.model.script;

import cn.tongdun.kunpeng.api.engine.model.VersionedEntity;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2019/12/16 下午3:21
 */
@Data
public class DynamicScript extends VersionedEntity {

    /**
     * 名称 script_name
     */
    private String scriptName;

    /**
     * 合作方 partner_code
     */
    private String partnerCode;

    /**
     * 产品 product_code
     */
    private String productCode;

    /**
     * 应用 app_name
     */
    private String appName;

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
     * 状态 1:已启用 0:未启用 status
     */
    private Integer status;

    /**
     * 是否已删除 is_deleted
     */
    private boolean deleted;

    /**
     * 脚本类型 groovy script_type
     */
    private String scriptType;

    /**
     * 代码 script_code
     */
    private String scriptCode;
}
