package cn.tongdun.kunpeng.api.engine.model.script;

import cn.tongdun.kunpeng.api.engine.model.StatusEntity;
import cn.tongdun.kunpeng.api.engine.model.VersionedEntity;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2019/12/16 下午3:21
 */
@Data
public class DynamicScript extends StatusEntity {

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
