package cn.tongdun.kunpeng.api.engine.model.policy.definition;

import cn.tongdun.kunpeng.api.engine.model.StatusEntity;
import cn.tongdun.kunpeng.api.engine.model.VersionedEntity;
import lombok.Data;


/**
 * 策略定义。将会放到缓存中，属性尽量简化，不要保留跟策略运行无关的属性
 */
@Data
public class PolicyDefinition extends StatusEntity {



    private String name;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件标识
     */
    private String eventId;

    /**
     * appName
     */
    private String appName;

    /**
     * appType
     */
    private String appType;

    /**
     * 合作方
     */
    private String partnerCode;

    /**
     * 服务类型
     */
    private String serviceType;

    /**
     * 是否模板
     */
    private boolean template;


    /**
     * 行业类型
     */
    private String industryType;

    /**
     * 产品标签
     */
    private String productTag;


    /**
     * 是否通用策略
     */
    private boolean common;


    /**
     * 当前调用版本
     */
    private String currVersion;
    private String currVersionUuid;
}